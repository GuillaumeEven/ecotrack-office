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
     * Calcula el estado de un desk para una fecha específica
     * - RESERVED: si existe una reserva CONFIRMED para ese día
     * - UNAVAILABLE: si el desk está inactivo (is_active=false)
     * - AVAILABLE: en otro caso
     */
    public ResourceStatus calculateDeskStatus(DeskEntity desk, LocalDate date) {
        if (desk == null || !desk.getIsActive()) {
            return ResourceStatus.UNAVAILABLE;
        }

        // Buscar si existe una reserva confirmada para este desk en esa fecha
        List<ReservationEntity> reservations = reservationRepository.findByResourceId(desk.getId());
        boolean hasConfirmedReservation = reservations.stream()
                .anyMatch(r -> r.getDate().equals(date) && r.getStatus() == ReservationStatus.CONFIRMED);

        return hasConfirmedReservation ? ResourceStatus.RESERVED : ResourceStatus.AVAILABLE;
    }

    /**
     * Calcula el estado de una room para una fecha específica
     *
     * Para DESK_AREA:
     * - Si occupancy >= 80% → RESERVED
     * - Si is_active=false → UNAVAILABLE
     * - Sino → AVAILABLE (pero con lógica de desbloqueo automático)
     *
     * Para MEETING_ROOM:
     * - Si totalmente reservado ese día → RESERVED
     * - Si is_active=false → UNAVAILABLE
     * - Sino → AVAILABLE
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
     * Calcula estado para MEETING_ROOM
     * - Si todas las reservas de la room para ese día → RESERVED
     * - Sino → AVAILABLE
     */
    private ResourceStatus calculateMeetingRoomStatus(RoomEntity room, LocalDate date) {
        List<ReservationEntity> roomReservations = reservationRepository.findByResourceId(room.getId());

        boolean isReservedThatDay = roomReservations.stream()
                .anyMatch(r -> r.getDate().equals(date) && r.getStatus() == ReservationStatus.CONFIRMED);

        return isReservedThatDay ? ResourceStatus.RESERVED : ResourceStatus.AVAILABLE;
    }

    /**
     * Calcula estado para DESK_AREA
     * Basado en tasa de ocupación y lógica de desbloqueo
     */
    private ResourceStatus calculateDeskAreaStatus(RoomEntity room, LocalDate date) {
        List<DeskEntity> desks = deskRepository.findByRoom_Id(room.getId());
        if (desks.isEmpty()) {
            return ResourceStatus.AVAILABLE;
        }

        // Contar desks reservados
        long reservedCount = desks.stream()
                .filter(desk -> calculateDeskStatus(desk, date) == ResourceStatus.RESERVED)
                .count();

        double occupancyRate = (double) reservedCount / desks.size();

        if (occupancyRate >= OCCUPANCY_THRESHOLD) {
            // Esta desk_area está llena (>= 80%)
            return ResourceStatus.RESERVED;
        }

        return ResourceStatus.AVAILABLE;
    }

    /**
     * Busca la siguiente desk_area disponible para desbloquear
     * Retorna la primera DESK_AREA inactiva después de la room actual,
     * respetando que is_active=true y que la room anterior esté en RESERVED
     */
    public RoomEntity getNextAvailableDeskArea(FloorEntity floor, LocalDate date) {
        List<RoomEntity> deskAreas = roomRepository.findByFloor_IdAndType(floor.getId(), RoomType.DESK_AREA);

        // Ordenar por ID para mantener orden consistente
        deskAreas.sort(Comparator.comparingLong(RoomEntity::getId));

        // Buscar la primera desk_area que esté UNAVAILABLE y is_active=true
        for (RoomEntity deskArea : deskAreas) {
            if (deskArea.getIsActive() && calculateRoomStatus(deskArea, date) == ResourceStatus.UNAVAILABLE) {
                return deskArea;
            }
        }

        return null;
    }

    /**
     * Retorna el floor con todos los rooms y desks con sus estados calculados para una fecha
     */
    public FloorWithStatusDto getFloorWithStatusForDate(Long floorId, LocalDate date) {
        FloorEntity floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new RuntimeException("Floor no encontrado: " + floorId));

        // Convertir floor a DTO
        FloorResponseDto floorDto = floorMapper.toResponseDto(floor);

        // Obtener todas las rooms del floor
        List<RoomEntity> rooms = roomRepository.findByFloor_Id(floorId);

        // Convertir cada room a RoomWithStatusDto
        List<RoomWithStatusDto> roomDtos = rooms.stream()
                .map(room -> convertRoomToWithStatusDto(room, date))
                .collect(Collectors.toList());

        return new FloorWithStatusDto(floorDto, roomDtos, date);
    }

    /**
     * Convierte una room entity a RoomWithStatusDto con todos sus desks
     */
    private RoomWithStatusDto convertRoomToWithStatusDto(RoomEntity room, LocalDate date) {
        RoomResponseDto roomDto = roomMapper.toResponseDto(room);

        // Obtener todos los desks de la room
        List<DeskEntity> desks = deskRepository.findByRoom_Id(room.getId());

        // Convertir cada desk a DeskWithStatusDto
        List<DeskWithStatusDto> deskDtos = desks.stream()
                .map(desk -> convertDeskToWithStatusDto(desk, date))
                .collect(Collectors.toList());

        // Calcular tasa de ocupación
        long reservedCount = deskDtos.stream()
                .filter(d -> d.getCalculatedStatus() == ResourceStatus.RESERVED)
                .count();
        double occupancyRate = desks.isEmpty() ? 0.0 : (double) reservedCount / desks.size();

        // Calcular estado de la room
        ResourceStatus roomStatus = calculateRoomStatus(room, date);

        return new RoomWithStatusDto(roomDto, deskDtos, occupancyRate, roomStatus);
    }

    /**
     * Convierte un desk entity a DeskWithStatusDto
     */
    private DeskWithStatusDto convertDeskToWithStatusDto(DeskEntity desk, LocalDate date) {
        DeskResponseDto deskDto = deskMapper.toResponseDto(desk);
        ResourceStatus status = calculateDeskStatus(desk, date);

        // Obtener el email del usuario que reservó (si aplica)
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
