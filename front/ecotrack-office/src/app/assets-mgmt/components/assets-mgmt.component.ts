import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Floor, Room, Desk, Reservation } from '../models';
import { FloorService, RoomService, DeskService, ReservationService } from '../services';

/**
 * Assets Management Component
 * Main page for managing floors, rooms, and desks
 * HTML is minimal (no Tailwind) for progressive styling
 */
@Component({
  selector: 'app-assets-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assets-mgmt.component.html',
  styleUrl: './assets-mgmt.component.css'
})
export class AssetsMgmtComponent implements OnInit {
  floors: Floor[] = [];
  rooms: Room[] = [];
  desks: Desk[] = [];
  reservations: Reservation[] = [];

  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;
  selectedDate: Date = new Date(); // Default to today

  loading = {
    floors: false,
    rooms: false,
    desks: false,
    reservations: false
  };

  errors = {
    floors: null as string | null,
    rooms: null as string | null,
    desks: null as string | null,
    reservations: null as string | null
  };

  constructor(
    private floorService: FloorService,
    private roomService: RoomService,
    private deskService: DeskService,
    private reservationService: ReservationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('🚀 AssetsMgmtComponent initialized, loading floors...');
    this.loadFloors();
    this.loadReservations();
  }

  /**
   * Load all floors
   */
  loadFloors(): void {
    console.log('📍 loadFloors() called');
    this.loading.floors = true;
    this.errors.floors = null;

    this.floorService.list().subscribe({
      next: (data) => {
        console.log('✅ Floors loaded:', data);
        this.floors = data;
        this.loading.floors = false;
        this.cdr.markForCheck(); // 🔴 Force Angular to detect changes
      },
      error: (err) => {
        console.error('❌ Error loading floors:', err);
        this.errors.floors = `Failed to load floors: ${err.message}`;
        this.loading.floors = false;
        this.cdr.markForCheck(); // 🔴 Force Angular to detect changes
        console.error(err);
      }
    });
  }

  /**
   * Load rooms for selected floor
   */
  onFloorSelect(floorId: number): void {
    console.log('🏢 onFloorSelect() called with floorId:', floorId);
    this.selectedFloorId = floorId;
    this.selectedRoomId = null;
    this.rooms = [];
    this.desks = [];

    this.loading.rooms = true;
    this.errors.rooms = null;

    this.roomService.listByFloor(floorId).subscribe({
      next: (data) => {
        console.log('✅ Rooms loaded:', data);
        this.rooms = data;
        this.loading.rooms = false;
        this.cdr.markForCheck(); // 🔴 Force Angular to detect changes
      },
      error: (err) => {
        console.error('❌ Error loading rooms:', err);
        this.errors.rooms = `Failed to load rooms: ${err.message}`;
        this.loading.rooms = false;
        this.cdr.markForCheck(); // 🔴 Force Angular to detect changes
        console.error(err);
      }
    });

    // Reload reservations for the new floor
    this.loadReservations();
  }

  /**
   * Load desks for selected room
   */
  onRoomSelect(roomId: number): void {
    console.log('🪑 onRoomSelect() called with roomId:', roomId);
    this.selectedRoomId = roomId;
    this.desks = [];

    this.loading.desks = true;
    this.errors.desks = null;

    this.deskService.listByRoom(roomId).subscribe({
      next: (data) => {
        console.log('✅ Desks loaded:', data);
        this.desks = data;
        this.loading.desks = false;
        this.cdr.markForCheck(); // 🔴 Force Angular to detect changes
      },
      error: (err) => {
        console.error('❌ Error loading desks:', err);
        this.errors.desks = `Failed to load desks: ${err.message}`;
        this.loading.desks = false;
        this.cdr.markForCheck(); // 🔴 Force Angular to detect changes
        console.error(err);
      }
    });
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

  /**
   * Load reservations for selected floor and date
   */
  loadReservations(): void {
    if (!this.selectedFloorId) {
      console.log('⚠️ No floor selected, skipping reservation load');
      return;
    }

    const dateISO = this.formatDateToISO(this.selectedDate);
    console.log('📍 loadReservations() called for floor', this.selectedFloorId, 'date', dateISO);

    this.loading.reservations = true;
    this.errors.reservations = null;

    this.reservationService.getByFloorAndDate(this.selectedFloorId, dateISO).subscribe({
      next: (data) => {
        console.log('✅ Reservations loaded:', data);
        this.reservations = data;
        this.loading.reservations = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('❌ Error loading reservations:', err);
        this.errors.reservations = `Failed to load reservations: ${err.message}`;
        this.loading.reservations = false;
        this.cdr.markForCheck();
        console.error(err);
      }
    });
  }

  /**
   * Get desk status based on reservations
   */
  getDeskStatus(desk: Desk): 'AVAILABLE' | 'RESERVED' | 'UNAVAILABLE' {
    const reservation = this.reservations.find(
      (r) => r.deskId === desk.id && r.status === 'CONFIRMED'
    );
    if (reservation) return 'RESERVED';
    if (!desk.isActive) return 'UNAVAILABLE';
    return 'AVAILABLE';
  }

  /**
   * Handle date change
   */
  onDateChange(event: any): void {
    const newDate = new Date(event.target.value);
    console.log('📅 Date changed to:', newDate);
    this.selectedDate = newDate;
    this.loadReservations();
  }
}
