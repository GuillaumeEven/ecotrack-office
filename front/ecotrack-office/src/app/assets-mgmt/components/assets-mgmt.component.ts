import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Floor, FloorWithStatus, RoomWithStatus, DeskWithStatus, ResourceStatus } from '../models';
import { FloorService } from '../services';

/**
 * Assets Management Component
 * Manages floors, rooms, and desks with dynamic status calculation
 * Consumes the global endpoint: GET /api/v1/floors/status/{organizationId}?date=YYYY-MM-DD
 * Returns all floors with progressive unlock logic applied
 */
@Component({
  selector: 'app-assets-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assets-mgmt.component.html',
  styleUrl: './assets-mgmt.component.css'
})
export class AssetsMgmtComponent implements OnInit {
  // Data from API
  allFloorsWithStatus: FloorWithStatus[] = [];

  // UI State
  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;
  selectedDate: Date = new Date(); // Default to today

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
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('🚀 AssetsMgmtComponent initialized, loading all floors with status...');
    this.loadAllFloorsWithStatus();
  }

  /**
   * Load all floors with status for a specific date using global progressive logic
   */
  private loadAllFloorsWithStatus(): void {
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
   */
  onFloorSelect(floorId: number): void {
    console.log('🏢 onFloorSelect() called with floorId:', floorId);
    this.selectedFloorId = floorId;
    this.selectedRoomId = null;
  }

  /**
   * Handle room selection (UI only, data already loaded)
   */
  onRoomSelect(roomId: number): void {
    console.log('🪑 onRoomSelect() called with roomId:', roomId);
    this.selectedRoomId = roomId;
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
   * Get occupancy percentage display
   */
  getOccupancyPercent(room: RoomWithStatus): string {
    return `${Math.round(room.occupancyRate * 100)}%`;
  }

  /**
   * Check if desk is available for reservation
   */
  isDeskAvailable(desk: DeskWithStatus): boolean {
    return desk.calculatedStatus === ResourceStatus.AVAILABLE;
  }

  /**
   * Check if desk is reserved
   */
  isDeskReserved(desk: DeskWithStatus): boolean {
    return desk.calculatedStatus === ResourceStatus.RESERVED;
  }

  /**
   * Check if desk is unavailable
   */
  isDeskUnavailable(desk: DeskWithStatus): boolean {
    return desk.calculatedStatus === ResourceStatus.UNAVAILABLE;
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
}
