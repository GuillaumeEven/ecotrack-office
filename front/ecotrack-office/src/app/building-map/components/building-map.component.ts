import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FloorWithStatus, RoomWithStatus, DeskWithStatus, ResourceStatus } from '../models';
import { FloorService } from '../services';
import { AuthService } from '../../services/auth.service';
import { DeskReservationDialogComponent } from '../dialogs/desk-reservation-dialog.component';

/**
 * Building Map Component
 * Interactive floor plan for browsing and making desk/room reservations
 * Consumes the global endpoint: GET /api/v1/floors/status/{organizationId}?date=YYYY-MM-DD
 * Returns all floors with progressive unlock logic applied
 */
@Component({
  selector: 'app-building-map',
  standalone: true,
  imports: [CommonModule, FormsModule, DeskReservationDialogComponent],
  templateUrl: './building-map.component.html',
  styleUrl: './building-map.component.css'
})
export class BuildingMapComponent implements OnInit {
  // Data from API
  allFloorsWithStatus: FloorWithStatus[] = [];

  // User context
  currentUserEmail: string = '';

  // UI State
  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;
  selectedDate: Date = new Date(); // Default to today

  // Dialog state
  isDialogOpen = false;
  selectedDeskForDialog: DeskWithStatus | null = null;
  isDialogForMeetingRoom = false; // Track if dialog is for meeting room

  // Enum for template
  ResourceStatus = ResourceStatus;

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
    console.log('🚀 BuildingMapComponent initialized, loading all floors with status...');
    this.loadAllFloorsWithStatus();
  }

  /**
   * Load all floors with status for a specific date using global progressive logic
   */
  private loadAllFloorsWithStatus(refreshCurrentRoom = false): void {
    this.loading.floors = true;
    this.errors.floors = null;

    const dateISO = this.formatDateToISO(this.selectedDate);
    console.log('📍 loadAllFloorsWithStatus() called for date', dateISO);

    this.floorService.getFloorsWithStatus(1, dateISO).subscribe({
      next: (data) => {
        console.log('✅ All floors with status loaded:', data);
        this.allFloorsWithStatus = data;

        // Auto-select first floor
        if (this.allFloorsWithStatus.length > 0 && !this.selectedFloorId) {
          this.selectedFloorId = this.allFloorsWithStatus[0].floor.id;
          console.log('🏢 Auto-selected first floor:', this.selectedFloorId);

          // Auto-select first room of the first floor
          const firstFloor = this.allFloorsWithStatus[0];
          if (firstFloor.rooms.length > 0 && !this.selectedRoomId) {
            this.selectedRoomId = firstFloor.rooms[0].room.id;
            console.log('🪑 Auto-selected first room:', this.selectedRoomId);
          }
        }

        // If refreshCurrentRoom is true, ensure the current room data is fresh
        if (refreshCurrentRoom && this.selectedRoomId) {
          console.log('🔄 Refreshing current room data after reservation:', this.selectedRoomId);
        }

        this.loading.floors = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('❌ Error loading floors with status:', err);
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
    console.log('🏢 onFloorSelect() called with floorId:', floorId);
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
    console.log('🪑 onRoomSelect() called with roomId:', roomId);
    this.selectedRoomId = roomId;
    // Force change detection when switching rooms to display updated statuses from progressive unlock
    this.cdr.markForCheck();
  }

  /**
   * Handle date change
   */
  onDateChange(event: any): void {
    const newDate = new Date(event.target.value);
    console.log('📅 Date changed to:', newDate);
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
  getSelectedFloor(): FloorWithStatus | undefined {
    return this.allFloorsWithStatus.find(f => f.floor.id === this.selectedFloorId);
  }

  /**
   * Get room by ID from selected floor
   */
  getRoomById(roomId: number): RoomWithStatus | undefined {
    return this.getSelectedFloor()?.rooms.find(r => r.room.id === roomId);
  }

  /**
   * Get desks for a specific room
   */
  getDesksByRoom(roomId: number | null): DeskWithStatus[] {
    if (!roomId) return [];
    return this.getRoomById(roomId)?.desks ?? [];
  }

  /**
   * Check if a room is a meeting room (vs desk area)
   */
  isMeetingRoom(room: RoomWithStatus): boolean {
    return room.room.roomType === 'MEETING_ROOM';
  }

  /**
   * Get the currently selected room
   */
  getSelectedRoom(): RoomWithStatus | null {
    if (!this.selectedRoomId) return null;
    return this.getRoomById(this.selectedRoomId) ?? null;
  }

  /**
   * Open meeting room reservation dialog
   */
  openMeetingRoomReservationDialog(room: RoomWithStatus): void {
    // Create a pseudo-DeskWithStatus for the dialog
    // The dialog will treat it as a reservation request for the room itself
    const pseudoDeskWithStatus: DeskWithStatus = {
      desk: room.room as any, // Room object has same id/name structure
      calculatedStatus: room.roomStatus,
      reservedBy: room.reservedBy,
      reservationId: room.reservationId
    };
    this.selectedDeskForDialog = pseudoDeskWithStatus;
    this.isDialogForMeetingRoom = true;
    this.isDialogOpen = true;

    // Force change detection to apply input bindings in dialog
    this.cdr.markForCheck();
  }

  /**
   * Get occupancy percentage display
   */
  getOccupancyPercent(room: RoomWithStatus): string {
    return `${Math.round(room.occupancyRate * 100)}%`;
  }

  /**
   * Check if selected room is unavailable
   */
  isSelectedRoomUnavailable(): boolean {
    if (!this.selectedRoomId) return false;
    const room = this.getRoomById(this.selectedRoomId);
    return room?.roomStatus === ResourceStatus.UNAVAILABLE;
  }

  /**
   * Check if desk is available for reservation
   */
  isDeskAvailable(desk: DeskWithStatus): boolean {
    return desk.calculatedStatus === ResourceStatus.AVAILABLE;
  }

  /**
   * Check if desk is unavailable
   */
  isDeskUnavailable(desk: DeskWithStatus): boolean {
    return desk.calculatedStatus === ResourceStatus.UNAVAILABLE;
  }

  /**
   * Check if the desk is reserved by the current user
   */
  isMyReservation(desk: DeskWithStatus): boolean {
    return desk.calculatedStatus === ResourceStatus.RESERVED && desk.reservedBy === this.currentUserEmail;
  }

  /**
   * Check if the desk is reserved by someone else
   */
  isOtherReservation(desk: DeskWithStatus): boolean {
    return desk.calculatedStatus === ResourceStatus.RESERVED && desk.reservedBy !== this.currentUserEmail;
  }

  /**
   * Check if user can cancel a reservation
   * Can cancel if: resource is reserved AND (it's my reservation OR user is ADMIN/TECHNICIAN)
   */
  canCancelReservation(desk: DeskWithStatus): boolean {
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
  getDeskStatusClass(desk: DeskWithStatus): string {
    if (this.isMyReservation(desk)) {
      return 'reserved-mine';
    }
    if (this.isOtherReservation(desk)) {
      return 'reserved-other';
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
  getFloorStatus(floor: FloorWithStatus): ResourceStatus {
    // If both desks and meeting rooms are occupied, floor is "reserved"
    if (floor.desksOccupied && floor.meetingRoomsOccupied) {
      return ResourceStatus.RESERVED;
    }
    // If desks are occupied, show as "reserved"
    if (floor.desksOccupied) {
      return ResourceStatus.RESERVED;
    }
    // If meeting rooms are occupied, show as "reserved"
    if (floor.meetingRoomsOccupied) {
      return ResourceStatus.RESERVED;
    }
    // Otherwise, floor has availability
    return ResourceStatus.AVAILABLE;
  }

  /**
   * Get CSS class for floor status indicator
   */
  getFloorStatusClass(floor: FloorWithStatus): string {
    const status = this.getFloorStatus(floor);
    return `status-${status.toLowerCase()}`;
  }

  /**
   * Get CSS class for room status indicator
   */
  getRoomStatusClass(room: RoomWithStatus): string {
    return `status-${room.roomStatus.toLowerCase()}`;
  }

  /**
   * Get readable status text
   */
  getStatusText(status: ResourceStatus): string {
    switch (status) {
      case ResourceStatus.AVAILABLE:
        return 'Available';
      case ResourceStatus.RESERVED:
        return 'Reserved';
      case ResourceStatus.UNAVAILABLE:
        return 'Unavailable';
      default:
        return 'Unknown';
    }
  }

  /**
   * Open desk reservation dialog
   * Don't open if desk is reserved by someone else
   */
  openDeskDialog(desk: DeskWithStatus): void {
    const userRole = this.authService.getRole();
    if (this.isOtherReservation(desk) && userRole !== 'ADMIN' && userRole !== 'TECHNICIAN') {
      return; // Don't open dialog for other people's reservations unless user is ADMIN or TECHNICIAN
    }
    this.selectedDeskForDialog = desk;
    this.isDialogForMeetingRoom = false;
    this.isDialogOpen = true;
  }

  /**
   * Close desk reservation dialog
   */
  closeDeskDialog(): void {
    this.isDialogOpen = false;
    this.selectedDeskForDialog = null;
    this.isDialogForMeetingRoom = false;
  }

  /**
   * Handle successful reservation - reload all floors to see progressive unlock changes
   * This forces the display to refresh after the backend recalculates meeting room statuses
   */
  onReservationSuccess(): void {
    console.log('🎉 Reservation successful, reloading all floors for progressive unlock...');
    // Reload floors with flag to refresh current room after data arrives
    this.loadAllFloorsWithStatus(true);
  }
}
