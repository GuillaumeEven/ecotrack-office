package com.ediae.ecotrack_office.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.ZoneConsolidationService;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class ZoneConsolidationServiceTest {

    @Mock
    private DeskRepository deskRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private FloorRepository floorRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ZoneConsolidationService service;

    private static final List<ReservationStatus> INACTIVE =
        List.of(ReservationStatus.CANCELLED, ReservationStatus.RELEASED);

    private OrganizationEntity org;
    private FloorEntity floor1;
    private FloorEntity floor2;
    private RoomEntity deskRoom;
    private DeskEntity desk;
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setUp() {
        org = new OrganizationEntity();
        org.setId(1L);

        floor1 = new FloorEntity(1, true, org);
        floor1.setId(10L);

        floor2 = new FloorEntity(2, true, org);
        floor2.setId(20L);

        deskRoom = new RoomEntity("Room A", ResourceStatus.AVAILABLE, null,
            RoomType.DESK_AREA, 40.0, floor1, 10);
        deskRoom.setId(100L);

        desk = new DeskEntity("Desk 1", ResourceStatus.RESERVED, null, deskRoom);
        desk.setId(1L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // onDeskReserved — DESK_AREA
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void onDeskReserved_occupancyBelowThreshold_doesNothing() {
        when(deskRepository.countByRoom_Id(100L)).thenReturn(10L);
        // 7/10 = 70% — below 80%
        when(reservationRepository.countActiveReservationsForRoom(eq(100L), eq(TODAY), any()))
            .thenReturn(7L);

        service.onDeskReserved(desk, TODAY);

        verify(roomRepository, never()).save(any());
    }

    @Test
    void onDeskReserved_occupancyAtThreshold_noBufferRoom_opensNextRoomOnSameFloor() {
        RoomEntity nextRoom = new RoomEntity("Room B", ResourceStatus.UNAVAILABLE, null,
            RoomType.DESK_AREA, 40.0, floor1, 10);
        nextRoom.setId(101L);

        when(deskRepository.countByRoom_Id(100L)).thenReturn(10L);
        // 8/10 = 80% — at threshold
        when(reservationRepository.countActiveReservationsForRoom(eq(100L), eq(TODAY), any()))
            .thenReturn(8L);
        // No other AVAILABLE desk_area room in the org
        when(roomRepository.findByFloor_Organization_IdAndTypeAndStatus(1L, RoomType.DESK_AREA, ResourceStatus.AVAILABLE))
            .thenReturn(List.of());
        // Next UNAVAILABLE on same floor
        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(10L, RoomType.DESK_AREA, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.of(nextRoom));

        service.onDeskReserved(desk, TODAY);

        verify(roomRepository).save(nextRoom);
        assert nextRoom.getStatus() == ResourceStatus.AVAILABLE;
    }

    @Test
    void onDeskReserved_occupancyAboveThreshold_availableBufferExists_doesNothing() {
        RoomEntity bufferRoom = new RoomEntity("Room Buffer", ResourceStatus.AVAILABLE, null,
            RoomType.DESK_AREA, 40.0, floor1, 10);
        bufferRoom.setId(102L);

        when(deskRepository.countByRoom_Id(100L)).thenReturn(10L);
        // 9/10 = 90%
        when(reservationRepository.countActiveReservationsForRoom(eq(100L), eq(TODAY), any()))
            .thenReturn(9L);
        // One AVAILABLE desk_area room exists in the org (the buffer)
        when(roomRepository.findByFloor_Organization_IdAndTypeAndStatus(1L, RoomType.DESK_AREA, ResourceStatus.AVAILABLE))
            .thenReturn(List.of(bufferRoom));
        // Buffer has 3/10 = 30% occupancy — below threshold
        when(deskRepository.countByRoom_Id(102L)).thenReturn(10L);
        when(reservationRepository.countActiveReservationsForRoom(eq(102L), eq(TODAY), any()))
            .thenReturn(3L);

        service.onDeskReserved(desk, TODAY);

        verify(roomRepository, never()).save(any());
    }

    @Test
    void onDeskReserved_noRoomOnSameFloor_opensRoomOnNextFloorByLevel() {
        RoomEntity roomOnFloor2 = new RoomEntity("Room F2", ResourceStatus.UNAVAILABLE, null,
            RoomType.DESK_AREA, 40.0, floor2, 10);
        roomOnFloor2.setId(200L);

        when(deskRepository.countByRoom_Id(100L)).thenReturn(10L);
        when(reservationRepository.countActiveReservationsForRoom(eq(100L), eq(TODAY), any()))
            .thenReturn(8L);
        when(roomRepository.findByFloor_Organization_IdAndTypeAndStatus(1L, RoomType.DESK_AREA, ResourceStatus.AVAILABLE))
            .thenReturn(List.of());
        // No room on floor1
        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(10L, RoomType.DESK_AREA, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.empty());
        // Floor list ordered by level
        when(floorRepository.findByOrganization_IdOrderByLevelAsc(1L))
            .thenReturn(List.of(floor1, floor2));
        // Room found on floor2
        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(20L, RoomType.DESK_AREA, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.of(roomOnFloor2));

        service.onDeskReserved(desk, TODAY);

        verify(roomRepository).save(roomOnFloor2);
        assert roomOnFloor2.getStatus() == ResourceStatus.AVAILABLE;
    }

    @Test
    void onDeskReserved_noUnavailableRoomAnywhere_doesNothing() {
        when(deskRepository.countByRoom_Id(100L)).thenReturn(10L);
        when(reservationRepository.countActiveReservationsForRoom(eq(100L), eq(TODAY), any()))
            .thenReturn(8L);
        when(roomRepository.findByFloor_Organization_IdAndTypeAndStatus(1L, RoomType.DESK_AREA, ResourceStatus.AVAILABLE))
            .thenReturn(List.of());
        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(10L, RoomType.DESK_AREA, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.empty());
        when(floorRepository.findByOrganization_IdOrderByLevelAsc(1L))
            .thenReturn(List.of(floor1));

        service.onDeskReserved(desk, TODAY);

        verify(roomRepository, never()).save(any());
    }

    @Test
    void onDeskReserved_roomWithNoDesks_doesNothing() {
        when(deskRepository.countByRoom_Id(100L)).thenReturn(0L);

        service.onDeskReserved(desk, TODAY);

        verify(reservationRepository, never()).countActiveReservationsForRoom(any(), any(), any());
        verify(roomRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // onMeetingRoomReserved — MEETING_ROOM
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void onMeetingRoomReserved_opensNextUnavailableMeetingRoomOnSameFloor() {
        RoomEntity meetingRoom = new RoomEntity("Meeting A", ResourceStatus.AVAILABLE, null,
            RoomType.MEETING_ROOM, 30.0, floor1, 12);
        meetingRoom.setId(300L);

        RoomEntity nextMeetingRoom = new RoomEntity("Meeting B", ResourceStatus.UNAVAILABLE, null,
            RoomType.MEETING_ROOM, 30.0, floor1, 12);
        nextMeetingRoom.setId(301L);

        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(10L, RoomType.MEETING_ROOM, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.of(nextMeetingRoom));

        service.onMeetingRoomReserved(meetingRoom);

        verify(roomRepository).save(nextMeetingRoom);
        assert nextMeetingRoom.getStatus() == ResourceStatus.AVAILABLE;
    }

    @Test
    void onMeetingRoomReserved_noRoomOnSameFloor_opensOnLowestOtherFloor() {
        RoomEntity meetingRoom = new RoomEntity("Meeting A", ResourceStatus.AVAILABLE, null,
            RoomType.MEETING_ROOM, 30.0, floor1, 12);
        meetingRoom.setId(300L);

        RoomEntity roomOnFloor2 = new RoomEntity("Meeting F2", ResourceStatus.UNAVAILABLE, null,
            RoomType.MEETING_ROOM, 30.0, floor2, 12);
        roomOnFloor2.setId(400L);

        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(10L, RoomType.MEETING_ROOM, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.empty());
        when(floorRepository.findByOrganization_IdOrderByLevelAsc(1L))
            .thenReturn(List.of(floor1, floor2));
        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(20L, RoomType.MEETING_ROOM, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.of(roomOnFloor2));

        service.onMeetingRoomReserved(meetingRoom);

        verify(roomRepository).save(roomOnFloor2);
        assert roomOnFloor2.getStatus() == ResourceStatus.AVAILABLE;
    }

    @Test
    void onMeetingRoomReserved_noUnavailableRoomAnywhere_doesNothing() {
        RoomEntity meetingRoom = new RoomEntity("Meeting A", ResourceStatus.AVAILABLE, null,
            RoomType.MEETING_ROOM, 30.0, floor1, 12);
        meetingRoom.setId(300L);

        when(roomRepository.findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(10L, RoomType.MEETING_ROOM, ResourceStatus.UNAVAILABLE))
            .thenReturn(Optional.empty());
        when(floorRepository.findByOrganization_IdOrderByLevelAsc(1L))
            .thenReturn(List.of(floor1));

        service.onMeetingRoomReserved(meetingRoom);

        verify(roomRepository, never()).save(any());
    }
}
