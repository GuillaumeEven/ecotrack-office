import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AuthService, FloorService } from '@services/index.service';
import { FloorWithStatusModel, RoomWithStatusModel, DeskWithStatusModel, ResourceStatusModel } from '@models/index.model';
import { DeskReservationModalComponent } from '@features/index.component';

/**
 * Building Map Component
 * Interactive floor plan for browsing and making desk/room reservations
 * Consumes the global endpoint: GET /api/v1/floors/status/{organizationId}?date=YYYY-MM-DD
 * Returns all floors with progressive unlock logic applied
 */
@Component({
  selector: 'app-building-map',
  standalone: true,
  imports: [CommonModule, FormsModule, DeskReservationModalComponent],
  templateUrl: './building-map.component.html',
  styleUrls: ['./building-map.component.css']
})
export class BuildingMapComponent implements OnInit {
  // Data from API
  allFloorsWithStatus: FloorWithStatusModel[] = [];

  // User context
  currentUserEmail: string = '';

  // UI State
  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;
  selectedDate: Date = new Date(); // Default to today

  // Dialog state
  isModalOpen = false;
  selectedDeskForModal: DeskWithStatusModel | null = null;
  isModalForMeetingRoom = false; // Track if dialog is for meeting room

  // Enum for template
  ResourceStatus = ResourceStatusModel;

  // Loading & Error states
  loading = {
    floors: false
  };

  errors = {
    floors: null as string | null
  };

  constructor(
    private floorService: FloorService,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {
    // Get current user email from JWT
    const email = this.authService.getEmail();
    this.currentUserEmail = email || '';
  }

  ngOnInit(): void {
    this.loadAllFloorsWithStatus();
  }

  /**
   * Load all floors with status for a specific date using global progressive logic
   */
  private loadAllFloorsWithStatus(refreshCurrentRoom = false): void {
    this.loading.floors = true;
    this.errors.floors = null;

    const dateISO = this.formatDateToISO(this.selectedDate);

    this.floorService.getFloorsWithStatus(1, dateISO).subscribe({
      next: (data) => {
        this.allFloorsWithStatus = data;

        // Auto-select first floor
        if (this.allFloorsWithStatus.length > 0 && !this.selectedFloorId) {
          this.selectedFloorId = this.allFloorsWithStatus[0].floor.id;

          // Auto-select first room of the first floor
          const firstFloor = this.allFloorsWithStatus[0];
          if (firstFloor.rooms.length > 0 && !this.selectedRoomId) {
            this.selectedRoomId = firstFloor.rooms[0].room.id;
          }
        }

        this.loading.floors = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errors.floors = `Failed to load floors: ${err.message}`;
        this.loading.floors = false;
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Handle floor selection
   * Force change detection to show updated meeting room statuses after progressive unlock
   */
  onFloorSelect(floorId: number): void {
    this.selectedFloorId = floorId;
    this.selectedRoomId = null;
    // Force change detection to display updated meeting room statuses
    this.cdr.markForCheck();
  }

  /**
   * Handle room selection (UI only, data already loaded)
   * Force change detection to show updated room statuses
   */
  onRoomSelect(roomId: number): void {
    this.selectedRoomId = roomId;
    // Force change detection when switching rooms to display updated statuses from progressive unlock
    this.cdr.markForCheck();
  }

  /**
   * Handle date change
   */
  onDateChange(event: any): void {
    const newDate = new Date(event.target.value);
    this.selectedDate = newDate;
    this.selectedFloorId = null;
    this.selectedRoomId = null;
    this.loadAllFloorsWithStatus();
  }

  /**
   * Format date to ISO string (YYYY-MM-DD)
   */
  private formatDateToISO(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  // ========== HELPER METHODS FOR TEMPLATE ==========

  /**
   * Get currently selected floor with status
   */
  getSelectedFloor(): FloorWithStatusModel | undefined {
    return this.allFloorsWithStatus.find(f => f.floor.id === this.selectedFloorId);
  }

  /**
   * Get room by ID from selected floor
   */
  getRoomById(roomId: number): RoomWithStatusModel | undefined {
    return this.getSelectedFloor()?.rooms.find(r => r.room.id === roomId);
  }

  /**
   * Get desks for a specific room
   */
  getDesksByRoom(roomId: number | null): DeskWithStatusModel[] {
    if (!roomId) return [];
    return this.getRoomById(roomId)?.desks ?? [];
  }

  /**
   * Check if a room is a meeting room (vs desk area)
   */
  isMeetingRoom(room: RoomWithStatusModel): boolean {
    return room.room.roomType === 'MEETING_ROOM';
  }

  /**
   * Get the currently selected room
   */
  getSelectedRoom(): RoomWithStatusModel | null {
    if (!this.selectedRoomId) return null;
    return this.getRoomById(this.selectedRoomId) ?? null;
  }

  /**
   * Open meeting room reservation dialog
   */
  openMeetingRoomReservationDialog(room: RoomWithStatusModel): void {
    // Create a pseudo-DeskWithStatus for the dialog
    // The dialog will treat it as a reservation request for the room itself
    const pseudoDeskWithStatus: DeskWithStatusModel = {
      desk: room.room as any, // Room object has same id/name structure
      calculatedStatus: room.roomStatus,
      reservedBy: room.reservedBy,
      reservationId: room.reservationId
    };
    this.selectedDeskForModal = pseudoDeskWithStatus;
    this.isModalForMeetingRoom = true;
    this.isModalOpen = true;

    // Force change detection to apply input bindings in dialog
    this.cdr.markForCheck();
  }

  /**
   * Get occupancy percentage display
   */
  getOccupancyPercent(room: RoomWithStatusModel): string {
    return `${Math.round(room.occupancyRate * 100)}%`;
  }

  /**
   * Check if selected room is unavailable
   */
  isSelectedRoomUnavailable(): boolean {
    if (!this.selectedRoomId) return false;
    const room = this.getRoomById(this.selectedRoomId);
    return room?.roomStatus === ResourceStatusModel.UNAVAILABLE;
  }

  /**
   * Check if desk is available for reservation
   */
  isDeskAvailable(desk: DeskWithStatusModel): boolean {
    return this.normalizeStatus(desk.calculatedStatus) === ResourceStatusModel.AVAILABLE;
  }

  /**
   * Check if desk is unavailable
   */
  isDeskUnavailable(desk: DeskWithStatusModel): boolean {
    return this.normalizeStatus(desk.calculatedStatus) === ResourceStatusModel.UNAVAILABLE;
  }

  /**
   * Check if desk is out of service
   */
  isDeskOutOfService(desk: DeskWithStatusModel): boolean {
    return this.normalizeStatus(desk.calculatedStatus) === ResourceStatusModel.OUT_OF_SERVICE;
  }

  /**
   * Check if the desk is reserved by the current user
   */
  isMyReservation(desk: DeskWithStatusModel): boolean {
    return this.normalizeStatus(desk.calculatedStatus) === ResourceStatusModel.RESERVED && desk.reservedBy === this.currentUserEmail;
  }

  /**
   * Check if the desk is reserved by someone else
   */
  isOtherReservation(desk: DeskWithStatusModel): boolean {
    return this.normalizeStatus(desk.calculatedStatus) === ResourceStatusModel.RESERVED && desk.reservedBy !== this.currentUserEmail;
  }

  /**
   * Normalize backend status values to the frontend enum format
   */
  private normalizeStatus(status: ResourceStatusModel | string | null | undefined): ResourceStatusModel | string {
    return String(status ?? '')
      .trim()
      .toUpperCase()
      .replace(/[-\s]+/g, '_');
  }

  /**
   * Get tooltip text for desk button
   */
  getDeskTooltip(desk: DeskWithStatusModel): string {
    if (this.isDeskOutOfService(desk)) {
      return 'Escritorio averiado';
    }
    if (this.isDeskUnavailable(desk)) {
      return 'No disponible';
    }
    if (this.isMyReservation(desk)) {
      return 'Tu reserva - Haz clic para cancelar';
    }
    if (this.isOtherReservation(desk)) {
      const reservedBy = desk.reservedBy ?? 'otro usuario';
      return `Reservado por ${reservedBy} - Haz clic para cancelar`;
    }
    return 'Disponible';
  }

  /**
   * Check if user can cancel a reservation
   * Can cancel if: resource is reserved AND (it's my reservation OR user is ADMIN/TECHNICIAN)
   */
  canCancelReservation(desk: DeskWithStatusModel): boolean {
    if (this.isMyReservation(desk)) {
      return true;
    }

    // Check if user is ADMIN or TECHNICIAN
    const userRole = this.authService.getRole();
    return userRole === 'ADMIN' || userRole === 'TECHNICIAN';
  }

  /**
   * Get CSS class for desk status
   */
  getDeskStatusClass(desk: DeskWithStatusModel): string {
    if (this.isMyReservation(desk)) {
      return 'reserved-mine';
    }
    if (this.isOtherReservation(desk)) {
      return 'reserved-other';
    }
    if (this.isDeskOutOfService(desk)) {
      return 'out-of-service';
    }
    if (this.isDeskAvailable(desk)) {
      return 'available';
    }
    return 'unavailable';
  }

  /**
   * Get the dominant status for a floor based on occupancy flags
   * UNAVAILABLE > RESERVED > AVAILABLE
   */
  getFloorStatus(floor: FloorWithStatusModel): ResourceStatusModel {
    // If both desks and meeting rooms are occupied, floor is "reserved"
    if (floor.desksOccupied && floor.meetingRoomsOccupied) {
      return ResourceStatusModel.RESERVED;
    }
    // If desks are occupied, show as "reserved"
    if (floor.desksOccupied) {
      return ResourceStatusModel.RESERVED;
    }
    // If meeting rooms are occupied, show as "reserved"
    if (floor.meetingRoomsOccupied) {
      return ResourceStatusModel.RESERVED;
    }
    // Otherwise, floor has availability
    return ResourceStatusModel.AVAILABLE;
  }

  /**
   * Get CSS class for floor status indicator
   */
  getFloorStatusClass(floor: FloorWithStatusModel): string {
    const status = this.getFloorStatus(floor);
    return `status-${status.toLowerCase().replace(/_/g, '-')}`;
  }

  /**
   * Get CSS class for room status indicator
   */
  getRoomStatusClass(room: RoomWithStatusModel): string {
    return `status-${room.roomStatus.toLowerCase().replace(/_/g, '-')}`;
  }

  /**
   * Get readable status text
   */
  getStatusText(status: ResourceStatusModel): string {
    switch (status) {
      case ResourceStatusModel.AVAILABLE:
        return 'Available';
      case ResourceStatusModel.RESERVED:
        return 'Reserved';
      case ResourceStatusModel.OUT_OF_SERVICE:
        return 'Out of service';
      case ResourceStatusModel.UNAVAILABLE:
        return 'Unavailable';
      default:
        return 'Unknown';
    }
  }

  /**
   * Open desk reservation dialog
   * Don't open if desk is reserved by someone else, out of service, or unavailable
   */
  openDeskDialog(desk: DeskWithStatusModel): void {
    // Can't reserve out-of-service or unavailable desks
    if (this.isDeskOutOfService(desk) || this.isDeskUnavailable(desk)) {
      return;
    }

    const userRole = this.authService.getRole();
    if (this.isOtherReservation(desk) && userRole !== 'ADMIN' && userRole !== 'TECHNICIAN') {
      return; // Don't open dialog for other people's reservations unless user is ADMIN or TECHNICIAN
    }
    this.selectedDeskForModal = desk;
    this.isModalForMeetingRoom = false;
    this.isModalOpen = true;
  }

  /**
   * Close desk reservation dialog
   */
  closeDeskDialog(): void {
    this.isModalOpen = false;
    this.selectedDeskForModal = null;
    this.isModalForMeetingRoom = false;
  }

  /**
   * Handle successful reservation - reload all floors to see progressive unlock changes
   * This forces the display to refresh after the backend recalculates meeting room statuses
   */
  onReservationSuccess(): void {
    // Reload floors with flag to refresh current room after data arrives
    this.loadAllFloorsWithStatus(true);
  }
}
