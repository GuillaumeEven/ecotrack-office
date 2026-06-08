import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Floor, Room, Desk } from '../models';
import { FloorService, RoomService, DeskService } from '../services';

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

  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;

  loading = {
    floors: false,
    rooms: false,
    desks: false
  };

  errors = {
    floors: null as string | null,
    rooms: null as string | null,
    desks: null as string | null
  };

  constructor(
    private floorService: FloorService,
    private roomService: RoomService,
    private deskService: DeskService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('🚀 AssetsMgmtComponent initialized, loading floors...');
    this.loadFloors();
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
}
