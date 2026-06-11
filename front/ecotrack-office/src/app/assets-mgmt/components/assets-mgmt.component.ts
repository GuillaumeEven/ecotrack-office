import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Floor, FloorWithStatus, RoomWithStatus, DeskWithStatus, ResourceStatus } from '../models';
import { FloorService } from '../services';

/**
 * Assets Management Component
 * Manages floors, rooms, and desks with dynamic status calculation
 * Consumes the new endpoint: GET /api/v1/floors/{id}/status?date=YYYY-MM-DD
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
  allFloors: Floor[] = [
    { id: 1, level: 0, isActive: true, organizationId: 1 },
    { id: 2, level: 1, isActive: true, organizationId: 1 },
    { id: 3, level: 2, isActive: true, organizationId: 1 }
  ];
  floorWithStatus: FloorWithStatus | null = null;

  // UI State
  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;
  selectedDate: Date = new Date(); // Default to today

  // Enum for template
  ResourceStatus = ResourceStatus;

  // Loading & Error states (simplified)
  loading = {
    floorStatus: false
  };

  errors = {
    floorStatus: null as string | null
  };

  constructor(
    private floorService: FloorService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('🚀 AssetsMgmtComponent initialized, loading floor 1 status...');
    // Auto-select first floor and load its status for today
    this.selectedFloorId = 1;
    this.loadFloorWithStatus();
  }

  /**
   * Load floor with status for a specific date
   * Single API call that returns everything calculated
   */
  private loadFloorWithStatus(): void {
    if (!this.selectedFloorId) {
      console.log('⚠️ No floor selected, skipping status load');
      return;
    }

    this.loading.floorStatus = true;
    this.errors.floorStatus = null;

    const dateISO = this.formatDateToISO(this.selectedDate);
    console.log('📍 loadFloorWithStatus() called for floor', this.selectedFloorId, 'date', dateISO);

    this.floorService.getFloorWithStatus(this.selectedFloorId, dateISO).subscribe({
      next: (data) => {
        console.log('✅ Floor with status loaded:', data);
        this.floorWithStatus = data;

        // Auto-select first room on initial load
        if (this.floorWithStatus.rooms.length > 0 && !this.selectedRoomId) {
          this.selectedRoomId = this.floorWithStatus.rooms[0].room.id;
          console.log('🪑 Auto-selected first room:', this.selectedRoomId);
        }

        this.loading.floorStatus = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('❌ Error loading floor status:', err);
        this.errors.floorStatus = `Failed to load floor status: ${err.message}`;
        this.loading.floorStatus = false;
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
    this.floorWithStatus = null;
    this.loadFloorWithStatus();
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
    this.loadFloorWithStatus();
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
   * Get room by ID from floorWithStatus
   */
  getRoomById(roomId: number): RoomWithStatus | undefined {
    return this.floorWithStatus?.rooms.find(r => r.room.id === roomId);
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
}
