package com.ediae.ecotrack_office.assets.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;

/**
 * Handles the zone consolidation policy:
 * - DESK_AREA: when a room reaches ≥80% occupancy and no other available room
 *   has a buffer below 80%, the next UNAVAILABLE room is opened (same floor first,
 *   then lowest floor).
 * - MEETING_ROOM: as soon as a meeting room is reserved, the next UNAVAILABLE
 *   meeting room on the same floor (or lowest floor) is opened.
 */
@Service
@Transactional
public class ZoneConsolidationService {

    private static final double OCCUPANCY_THRESHOLD = 0.80;

    // Statuses that count as inactive reservations (not occupying the resource)
    private static final List<ReservationStatus> INACTIVE_STATUSES =
        List.of(ReservationStatus.CANCELLED, ReservationStatus.RELEASED);

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Called after a desk reservation is confirmed.
     * Checks if the room crossed the 80% occupancy threshold and, if no other
     * available room acts as a buffer, opens the next room.
     */
    public void onDeskReserved(DeskEntity desk, LocalDate date) {
        RoomEntity room = desk.getRoom();
        long totalDesks = deskRepository.countByRoom_Id(room.getId());
        if (totalDesks == 0) return;

        long activeReservations = reservationRepository.countActiveReservationsForRoom(
            room.getId(), date, INACTIVE_STATUSES);
        double occupancy = (double) activeReservations / totalDesks;

        if (occupancy < OCCUPANCY_THRESHOLD) return;

        Long orgId = room.getFloor().getOrganization().getId();
        List<RoomEntity> availableRooms = roomRepository.findByFloor_Organization_IdAndTypeAndStatus(
            orgId, RoomType.DESK_AREA, ResourceStatus.AVAILABLE);

        boolean hasBufferRoom = availableRooms.stream().anyMatch(r -> {
            long total = deskRepository.countByRoom_Id(r.getId());
            if (total == 0) return true; // empty room still has full capacity
            long reserved = reservationRepository.countActiveReservationsForRoom(
                r.getId(), date, INACTIVE_STATUSES);
            return (double) reserved / total < OCCUPANCY_THRESHOLD;
        });

        if (!hasBufferRoom) {
            openNextRoom(room.getFloor(), orgId, RoomType.DESK_AREA);
        }
    }

    /**
     * Called after a meeting room reservation is confirmed.
     * Always opens the next UNAVAILABLE meeting room to maintain one available buffer.
     */
    public void onMeetingRoomReserved(RoomEntity room) {
        Long orgId = room.getFloor().getOrganization().getId();
        openNextRoom(room.getFloor(), orgId, RoomType.MEETING_ROOM);
    }

    /**
     * Finds and opens the next UNAVAILABLE room of the given type.
     * Priority: same floor first, then other floors ordered by level ascending.
     */
    private void openNextRoom(FloorEntity currentFloor, Long orgId, RoomType type) {
        // Same floor first
        Optional<RoomEntity> candidate = roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(
            currentFloor.getId(), type, ResourceStatus.UNAVAILABLE);

        if (candidate.isPresent()) {
            candidate.get().setStatus(ResourceStatus.AVAILABLE);
            roomRepository.save(candidate.get());
            return;
        }

        // Other floors, lowest level first
        List<FloorEntity> floors = floorRepository.findByOrganization_IdOrderByLevelAsc(orgId);
        for (FloorEntity floor : floors) {
            if (floor.getId().equals(currentFloor.getId())) continue;
            Optional<RoomEntity> crossFloorCandidate = roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(
                floor.getId(), type, ResourceStatus.UNAVAILABLE);
            if (crossFloorCandidate.isPresent()) {
                crossFloorCandidate.get().setStatus(ResourceStatus.AVAILABLE);
                roomRepository.save(crossFloorCandidate.get());
                return;
            }
        }
        // No more UNAVAILABLE rooms of this type — all are already open or none exist
    }
}
