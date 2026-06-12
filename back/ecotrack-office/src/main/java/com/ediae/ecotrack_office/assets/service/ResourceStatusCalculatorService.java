package com.ediae.ecotrack_office.assets.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.dto.DeskResponseDto;
import com.ediae.ecotrack_office.assets.dto.DeskWithStatusDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;
import com.ediae.ecotrack_office.assets.dto.FloorWithStatusDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;
import com.ediae.ecotrack_office.assets.dto.RoomWithStatusDto;
import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.mapper.DeskMapper;
import com.ediae.ecotrack_office.assets.mapper.FloorMapper;
import com.ediae.ecotrack_office.assets.mapper.RoomMapper;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;

@Service
public class ResourceStatusCalculatorService {

    private static final double OCCUPANCY_THRESHOLD = 0.8;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DeskMapper deskMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private FloorMapper floorMapper;

    /**
     * Calculates floor statuses for all floors in an organization for a given date
     * Applies global cascading unlock logic: DESK_AREA and MEETING_ROOM are progressively unlocked
     * based on the occupancy of previous resources across all floors.
     */
    public List<FloorWithStatusDto> calculateFloorsStatusForDate(Long organizationId, LocalDate date) {
        List<FloorWithStatusDto> floorsWithStatus = new java.util.ArrayList<>();

        // Get all floors for the organization
        List<FloorEntity> floors = floorRepository.findByOrganizationId(organizationId);
        floors.sort(Comparator.comparingLong(FloorEntity::getId));

        // Step 1: Build complete list of all DESK_AREA and MEETING_ROOM rooms across all floors
        List<RoomEntity> allDeskAreas = new java.util.ArrayList<>();
        List<RoomEntity> allMeetingRooms = new java.util.ArrayList<>();

        for (FloorEntity floor : floors) {
            List<RoomEntity> floorRooms = roomRepository.findByFloor_Id(floor.getId());
            floorRooms.sort(Comparator.comparingLong(RoomEntity::getId));

            for (RoomEntity room : floorRooms) {
                if (RoomType.DESK_AREA.equals(room.getType())) {
                    allDeskAreas.add(room);
                } else if (RoomType.MEETING_ROOM.equals(room.getType())) {
                    allMeetingRooms.add(room);
                }
            }
        }

        // Step 2: Calculate statuses with progressive unlock logic
        List<RoomWithStatusDto> deskAreaStatusList = calculateProgressiveDeskAreaStatus(allDeskAreas, date);
        List<RoomWithStatusDto> meetingRoomStatusList = calculateProgressiveMeetingRoomStatus(allMeetingRooms, date);

        // Step 3: Build floor DTOs with their rooms and occupancy flags
        for (FloorEntity floor : floors) {
            FloorResponseDto floorDto = floorMapper.toResponseDto(floor);
            List<RoomWithStatusDto> floorRooms = new java.util.ArrayList<>();

            // Collect this floor's desk areas and meeting rooms
            for (RoomWithStatusDto deskArea : deskAreaStatusList) {
                if (deskArea.getRoom().getFloorId().equals(floor.getId())) {
                    floorRooms.add(deskArea);
                }
            }
            for (RoomWithStatusDto meetingRoom : meetingRoomStatusList) {
                if (meetingRoom.getRoom().getFloorId().equals(floor.getId())) {
                    floorRooms.add(meetingRoom);
                }
            }

            FloorWithStatusDto floorWithStatus = new FloorWithStatusDto(floorDto, floorRooms, date);

            // Calculate floor occupancy flags
            calculateFloorOccupancyFlags(floorWithStatus, deskAreaStatusList, meetingRoomStatusList, floor.getId());

            floorsWithStatus.add(floorWithStatus);
        }

        return floorsWithStatus;
    }

    /**
     * Calculates DESK_AREA statuses with progressive unlock logic:
     * - All start as UNAVAILABLE
     * - First DESK_AREA becomes AVAILABLE
     * - Each following DESK_AREA becomes AVAILABLE only if previous has occupancy >= 80%
     */
    private List<RoomWithStatusDto> calculateProgressiveDeskAreaStatus(List<RoomEntity> allDeskAreas, LocalDate date) {
        List<RoomWithStatusDto> result = new java.util.ArrayList<>();
        RoomWithStatusDto previousDeskArea = null;

        for (int i = 0; i < allDeskAreas.size(); i++) {
            RoomEntity deskArea = allDeskAreas.get(i);
            RoomWithStatusDto deskAreaDto = convertRoomToWithStatusDto(deskArea, date);

            // First DESK_AREA is always AVAILABLE
            if (i == 0) {
                deskAreaDto = new RoomWithStatusDto(
                        deskAreaDto.getRoom(),
                        deskAreaDto.getDesks(),
                        deskAreaDto.getOccupancyRate(),
                        ResourceStatus.AVAILABLE
                );
            } else if (previousDeskArea != null && previousDeskArea.getOccupancyRate() >= OCCUPANCY_THRESHOLD) {
                // If previous DESK_AREA has occupancy >= 80%, this one becomes AVAILABLE
                deskAreaDto = new RoomWithStatusDto(
                        deskAreaDto.getRoom(),
                        deskAreaDto.getDesks(),
                        deskAreaDto.getOccupancyRate(),
                        ResourceStatus.AVAILABLE
                );
            } else {
                // Otherwise, keep as UNAVAILABLE
                deskAreaDto = new RoomWithStatusDto(
                        deskAreaDto.getRoom(),
                        deskAreaDto.getDesks(),
                        deskAreaDto.getOccupancyRate(),
                        ResourceStatus.UNAVAILABLE
                );
            }

            result.add(deskAreaDto);
            previousDeskArea = deskAreaDto;
        }

        return result;
    }

    /**
     * Calculates MEETING_ROOM statuses with progressive unlock logic:
     * - All start as UNAVAILABLE (not reserved is considered AVAILABLE by base logic)
     * - First MEETING_ROOM not reserved becomes AVAILABLE
     * - Each following MEETING_ROOM becomes AVAILABLE only if previous is RESERVED
     */
    private List<RoomWithStatusDto> calculateProgressiveMeetingRoomStatus(List<RoomEntity> allMeetingRooms, LocalDate date) {
        List<RoomWithStatusDto> result = new java.util.ArrayList<>();
        RoomWithStatusDto previousMeetingRoom = null;

        for (int i = 0; i < allMeetingRooms.size(); i++) {
            RoomEntity meetingRoom = allMeetingRooms.get(i);
            RoomWithStatusDto meetingRoomDto = convertRoomToWithStatusDto(meetingRoom, date);

            // First MEETING_ROOM: if not reserved, AVAILABLE
            if (i == 0) {
                if (meetingRoomDto.getRoomStatus() != ResourceStatus.RESERVED) {
                    meetingRoomDto = new RoomWithStatusDto(
                            meetingRoomDto.getRoom(),
                            meetingRoomDto.getDesks(),
                            meetingRoomDto.getOccupancyRate(),
                            ResourceStatus.AVAILABLE
                    );
                }
            } else if (previousMeetingRoom != null && previousMeetingRoom.getRoomStatus() == ResourceStatus.RESERVED) {
                // If previous MEETING_ROOM is RESERVED, this one becomes AVAILABLE (if not reserved itself)
                if (meetingRoomDto.getRoomStatus() != ResourceStatus.RESERVED) {
                    meetingRoomDto = new RoomWithStatusDto(
                            meetingRoomDto.getRoom(),
                            meetingRoomDto.getDesks(),
                            meetingRoomDto.getOccupancyRate(),
                            ResourceStatus.AVAILABLE
                    );
                }
            } else {
                // Otherwise, mark as UNAVAILABLE
                meetingRoomDto = new RoomWithStatusDto(
                        meetingRoomDto.getRoom(),
                        meetingRoomDto.getDesks(),
                        meetingRoomDto.getOccupancyRate(),
                        ResourceStatus.UNAVAILABLE
                );
            }

            result.add(meetingRoomDto);
            previousMeetingRoom = meetingRoomDto;
        }

        return result;
    }

    /**
     * Calculates and sets floor-level occupancy flags:
     * - desksOccupied: true if the last DESK_AREA of this floor has occupancy >= 80%
     * - meetingRoomsOccupied: true if the last MEETING_ROOM of this floor is RESERVED
     */
    private void calculateFloorOccupancyFlags(FloorWithStatusDto floorWithStatus,
                                              List<RoomWithStatusDto> allDeskAreas,
                                              List<RoomWithStatusDto> allMeetingRooms,
                                              Long floorId) {
        // Find this floor's desk areas and meeting rooms
        RoomWithStatusDto lastDeskArea = null;
        RoomWithStatusDto lastMeetingRoom = null;

        for (RoomWithStatusDto deskArea : allDeskAreas) {
            if (deskArea.getRoom().getFloorId().equals(floorId)) {
                lastDeskArea = deskArea;
            }
        }

        for (RoomWithStatusDto meetingRoom : allMeetingRooms) {
            if (meetingRoom.getRoom().getFloorId().equals(floorId)) {
                lastMeetingRoom = meetingRoom;
            }
        }

        // Set desksOccupied flag
        boolean desksOccupied = lastDeskArea != null
                && lastDeskArea.getOccupancyRate() >= OCCUPANCY_THRESHOLD
                && lastDeskArea.getRoomStatus() == ResourceStatus.AVAILABLE;
        floorWithStatus.setDesksOccupied(desksOccupied);

        // Set meetingRoomsOccupied flag
        boolean meetingRoomsOccupied = lastMeetingRoom != null
                && lastMeetingRoom.getRoomStatus() == ResourceStatus.RESERVED;
        floorWithStatus.setMeetingRoomsOccupied(meetingRoomsOccupied);
    }

    /**
     * Calculates desk status for a specific date
     * - RESERVED: if confirmed reservation exists for that day
     * - UNAVAILABLE: if desk is inactive (is_active=false)
     * - AVAILABLE: otherwise
     */
    public ResourceStatus calculateDeskStatus(DeskEntity desk, LocalDate date) {
        if (desk == null || !desk.getIsActive()) {
            return ResourceStatus.UNAVAILABLE;
        }

        List<ReservationEntity> reservations = reservationRepository.findByResourceId(desk.getId());
        boolean hasConfirmedReservation = reservations.stream()
                .anyMatch(r -> r.getDate().equals(date) && r.getStatus() == ReservationStatus.CONFIRMED);

        return hasConfirmedReservation ? ResourceStatus.RESERVED : ResourceStatus.AVAILABLE;
    }

    /**
     * Calculates room status for a specific date
     *
     * For DESK_AREA:
     * - If occupancy >= 80% → RESERVED
     * - If is_active=false → UNAVAILABLE
     * - Otherwise → AVAILABLE
     *
     * For MEETING_ROOM:
     * - If reserved that day → RESERVED
     * - If is_active=false → UNAVAILABLE
     * - Otherwise → AVAILABLE
     */
    public ResourceStatus calculateRoomStatus(RoomEntity room, LocalDate date) {
        if (room == null || !room.getIsActive()) {
            return ResourceStatus.UNAVAILABLE;
        }

        if (RoomType.MEETING_ROOM.equals(room.getType())) {
            return calculateMeetingRoomStatus(room, date);
        } else if (RoomType.DESK_AREA.equals(room.getType())) {
            return calculateDeskAreaStatus(room, date);
        }

        return ResourceStatus.AVAILABLE;
    }

    /**
     * Calculates status for MEETING_ROOM
     * - If reserved that day → RESERVED
     * - Otherwise → AVAILABLE
     */
    private ResourceStatus calculateMeetingRoomStatus(RoomEntity room, LocalDate date) {
        List<ReservationEntity> roomReservations = reservationRepository.findByResourceId(room.getId());

        boolean isReservedThatDay = roomReservations.stream()
                .anyMatch(r -> r.getDate().equals(date) && r.getStatus() == ReservationStatus.CONFIRMED);

        return isReservedThatDay ? ResourceStatus.RESERVED : ResourceStatus.AVAILABLE;
    }

    /**
     * Calculates status for DESK_AREA
     * Based on occupancy rate: if >= 80% → RESERVED, otherwise → AVAILABLE
     */
    private ResourceStatus calculateDeskAreaStatus(RoomEntity room, LocalDate date) {
        List<DeskEntity> desks = deskRepository.findByRoom_Id(room.getId());
        if (desks.isEmpty()) {
            return ResourceStatus.AVAILABLE;
        }

        long reservedCount = desks.stream()
                .filter(desk -> calculateDeskStatus(desk, date) == ResourceStatus.RESERVED)
                .count();

        double occupancyRate = (double) reservedCount / desks.size();

        return occupancyRate >= OCCUPANCY_THRESHOLD ? ResourceStatus.RESERVED : ResourceStatus.AVAILABLE;
    }

    /**
     * Converts a room entity to RoomWithStatusDto with all desks
     */
    private RoomWithStatusDto convertRoomToWithStatusDto(RoomEntity room, LocalDate date) {
        RoomResponseDto roomDto = roomMapper.toResponseDto(room);
        List<DeskEntity> desks = deskRepository.findByRoom_Id(room.getId());

        List<DeskWithStatusDto> deskDtos = desks.stream()
                .map(desk -> convertDeskToWithStatusDto(desk, date))
                .collect(Collectors.toList());

        long reservedCount = deskDtos.stream()
                .filter(d -> d.getCalculatedStatus() == ResourceStatus.RESERVED)
                .count();
        double occupancyRate = desks.isEmpty() ? 0.0 : (double) reservedCount / desks.size();

        ResourceStatus roomStatus = calculateRoomStatus(room, date);

        return new RoomWithStatusDto(roomDto, deskDtos, occupancyRate, roomStatus);
    }

    /**
     * Converts a desk entity to DeskWithStatusDto
     */
    private DeskWithStatusDto convertDeskToWithStatusDto(DeskEntity desk, LocalDate date) {
        DeskResponseDto deskDto = deskMapper.toResponseDto(desk);
        ResourceStatus status = calculateDeskStatus(desk, date);

        String reservedBy = null;
        if (status == ResourceStatus.RESERVED) {
            List<ReservationEntity> reservations = reservationRepository.findByResourceId(desk.getId());
            Optional<ReservationEntity> reservation = reservations.stream()
                    .filter(r -> r.getDate().equals(date) && r.getStatus() == ReservationStatus.CONFIRMED)
                    .findFirst();

            if (reservation.isPresent()) {
                reservedBy = reservation.get().getUser().getEmail();
            }
        }

        return new DeskWithStatusDto(deskDto, status, reservedBy);
    }

}
