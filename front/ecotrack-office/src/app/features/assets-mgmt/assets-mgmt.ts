import { Component, ChangeDetectorRef } from '@angular/core';
import { Desk, Floor, ResourceStatus, Room } from '../../building-map/models';
import { forkJoin } from 'rxjs';

import { FloorService } from '../../building-map/services/floor.service';
import { RoomService } from '../../building-map/services/room.service';
import { DeskService } from '../../building-map/services/desk.service';
import { CommonModule, UpperCasePipe } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';


@Component({
  selector: 'app-assets-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, UpperCasePipe],
  templateUrl: './assets-mgmt.html',
  styleUrls: ['./assets-mgmt.css'],
})
export class AssetsMgmt {
  // Raw data from API (unfiltered)
  allFloors: Floor[] = [];
  allRooms: Room[] = [];
  allDesks: Desk[] = [];

  // Filter state
  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;
  selectedDeskId: number | null = null;
  editingFloor: Floor | null = null;
  editingRoom: Room | null = null;
  editingDesk: Desk | null = null;
  tabs: ('floors' | 'rooms' | 'desks')[] = ['floors', 'rooms', 'desks'];
  activeTab: 'floors' | 'rooms' | 'desks' = 'floors';

  // Dialog state
  isFloorDialogOpen = false;
  isRoomDialogOpen = false;
  isDeskDialogOpen = false;
  isDeleteDialogOpen = false;

  // Enum for template
  ResourceStatus = ResourceStatus;

  // Form groups
  floorForm!: FormGroup;
  roomForm!: FormGroup;
  deskForm!: FormGroup;

  // Delete modal properties
  deleteItemType: string = '';
  deleteItemName: string = '';
  deleteItemId: number | null = null;


  // Loading and error state
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private floorService: FloorService,
    private roomService: RoomService,
    private deskService: DeskService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.initializeForms();
  }


  ngOnInit(): void {
    // Fetch all data in parallel on init
    this.loadAllData();
  }

  // ========== GETTERS FOR FILTERED DATA ==========

  get floors(): Floor[] {
    return this.allFloors;
  }

  get filteredRooms(): Room[] {
    console.log('[DEBUG filteredRooms] selectedFloorId:', this.selectedFloorId);
    console.log('[DEBUG filteredRooms] allRooms:', this.allRooms);

    if (!this.selectedFloorId) {
      console.log('[DEBUG filteredRooms] No floor selected, returning all rooms');
      return this.allRooms; // Show all if no floor selected
    }

    const filtered = this.allRooms.filter(room => {
      console.log(`[DEBUG filteredRooms] Room ${room.id} (${room.name}): floorId=${room.floorId}, match=${room.floorId === this.selectedFloorId}`);
      return room.floorId === this.selectedFloorId;
    });
    console.log('[DEBUG filteredRooms] Filtered result:', filtered);
    return filtered;
  }

  get filteredDesks(): Desk[] {
    if (!this.selectedRoomId) {
      return this.allDesks; // Show all if no room selected
    }
    return this.allDesks.filter(desk => desk.roomId === this.selectedRoomId);
  }

  get deskAreas(): Room[] {
    // Only return rooms with roomType DESK_AREA
    return this.allRooms.filter(room => room.roomType === 'DESK_AREA');
  }

  // ========== TAB MANAGEMENT ==========

  setActiveTab(tab: 'floors' | 'rooms' | 'desks') {
    this.activeTab = tab;
    // No need to fetch data - already loaded at init
  }


  // ========== FORM INITIALIZATION ==========

  initializeForms() {
    this.floorForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      level: ['', [Validators.required, Validators.min(0)]],
      isActive: [true]
    });

    this.roomForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      floorId: ['', Validators.required],
      roomType: ['', Validators.required],
      capacity: ['', [Validators.required, Validators.min(1)]],
      surfaceArea: ['', [Validators.required, Validators.min(0.01)]],
      equipmentList: [''],
      isActive: [true]
    });

    this.deskForm = this.fb.group({
      roomId: ['', Validators.required],
      code: ['', [Validators.required, Validators.minLength(1)]],
      isAvailable: [true],
      assignedTo: ['']
    });
  }

  // ========== FORM SUBMISSION ==========

  submitFloor() {
    if (this.floorForm.invalid) return;

    const floorData = this.floorForm.value;

    if (this.editingFloor) {
      // Update
      this.floorService.update(this.editingFloor.id, floorData).subscribe({
        next: (updated) => {
          const idx = this.allFloors.findIndex(f => f.id === updated.id);
          if (idx !== -1) this.allFloors[idx] = updated;
          this.closeFloorDialog();
        },
        error: (err) => this.errorMessage = 'Error updating floor'
      });
    } else {
      // Create
      this.floorService.create(floorData).subscribe({
        next: (created) => {
          this.allFloors.push(created);
          this.closeFloorDialog();
        },
        error: (err) => this.errorMessage = 'Error creating floor'
      });
    }
  }

  submitRoom() {
    if (this.roomForm.invalid) return;

    // Use getRawValue() to include disabled fields (e.g., floorId when editing)
    const roomData = this.roomForm.getRawValue();

    if (this.editingRoom) {
      this.roomService.update(this.editingRoom.id, roomData).subscribe({
        next: (updated) => {
          const idx = this.allRooms.findIndex(r => r.id === updated.id);
          if (idx !== -1) this.allRooms[idx] = updated;
          this.closeRoomDialog();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = 'Error updating room';
          this.cdr.markForCheck();
        }
      });
    } else {
      this.roomService.create(roomData).subscribe({
        next: (created) => {
          this.allRooms.push(created);
          this.closeRoomDialog();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = 'Error creating room';
          this.cdr.markForCheck();
        }
      });
    }
  }

  submitDesk() {
    if (this.deskForm.invalid) return;

    const deskData = this.deskForm.value;

    if (this.editingDesk) {
      this.deskService.update(this.editingDesk.id, deskData).subscribe({
        next: (updated) => {
          const idx = this.allDesks.findIndex(d => d.id === updated.id);
          if (idx !== -1) this.allDesks[idx] = updated;
          this.closeDeskDialog();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = 'Error updating desk';
          this.cdr.markForCheck();
        }
      });
    } else {
      this.deskService.create(deskData).subscribe({
        next: (created) => {
          this.allDesks.push(created);
          this.closeDeskDialog();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = 'Error creating desk';
          this.cdr.markForCheck();
        }
      });
    }
  }

  // Methods to confirm deletion
  confirmDelete() {
    if (this.deleteItemId === null) return;

    switch (this.deleteItemType) {
      case 'floor':
        this.floorService.delete(this.deleteItemId).subscribe({
          next: () => {
            this.allFloors = this.allFloors.filter(f => f.id !== this.deleteItemId);
            this.closeDeleteDialog();
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage = 'Error deleting floor';
            this.cdr.markForCheck();
          }
        });
        break;
      case 'room':
        this.roomService.delete(this.deleteItemId).subscribe({
          next: () => {
            this.allRooms = this.allRooms.filter(r => r.id !== this.deleteItemId);
            this.closeDeleteDialog();
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage = 'Error deleting room';
            this.cdr.markForCheck();
          }
        });
        break;
      case 'desk':
        this.deskService.delete(this.deleteItemId).subscribe({
          next: () => {
            this.allDesks = this.allDesks.filter(d => d.id !== this.deleteItemId);
            this.closeDeleteDialog();
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage = 'Error deleting desk';
            this.cdr.markForCheck();
          }
        });
        break;
    }
  }


  // ========== HELPER METHODS FOR MODALS ==========


  // Handle floor selection change from dropdown
  onFloorChange() {
    // Reset room and desk selection
    this.selectedRoomId = null;
    this.selectedDeskId = null;
    this.cdr.markForCheck();
    // Filtered rooms will update via getter
  }

  // Handle room selection change from dropdown
  onRoomChange() {
    // Reset desk selection
    this.selectedDeskId = null;
    this.cdr.markForCheck();
    // Filtered desks will update via getter
  }

  // Methods to get selected entities
  getSelectedFloor(): Floor | undefined {
    return this.allFloors.find(floor => floor.id === this.selectedFloorId);
  }

  getSelectedRoom(): Room | undefined {
    return this.allRooms.find(room => room.id === this.selectedRoomId);
  }

  getSelectedDesk(): Desk | undefined {
    return this.allDesks.find(desk => desk.id === this.selectedDeskId);
  }

  // Methods to open dialogs
  openFloorDialog(floor?: Floor | null) {
    this.editingFloor = floor || null;
    if (floor) {
      this.floorForm.patchValue({
        name: floor.name,
        level: floor.level,
        isActive: floor.isActive
      });
    } else {
      this.floorForm.reset({
        name: '',
        level: '',
        isActive: true
      });
    }
    this.isFloorDialogOpen = true;
  }

  openRoomDialog(room?: Room | null) {
    this.editingRoom = room || null;
    if (room) {
      this.roomForm.patchValue({
        name: room.name,
        floorId: room.floorId,
        roomType: room.roomType,
        capacity: room.capacity,
        surfaceArea: room.surfaceArea,
        equipmentList: room.equipmentList || '',
        isActive: room.isActive
      });
      // Disable floor selection when editing (floor is immutable)
      this.roomForm.get('floorId')?.disable();
    } else {
      this.roomForm.reset({
        name: '',
        floorId: '',
        roomType: '',
        capacity: '',
        surfaceArea: '',
        equipmentList: '',
        isActive: true
      });
      // Enable floor selection when creating new room
      this.roomForm.get('floorId')?.enable();
    }
    this.isRoomDialogOpen = true;
  }

  openDeskDialog(desk?: Desk | null) {
    this.editingDesk = desk || null;
    this.isDeskDialogOpen = true;
  }

  openDeleteDialog(type: 'floor' | 'room' | 'desk', id: number, name?: string) {
    this.deleteItemType = type;
    this.deleteItemId = id;
    this.deleteItemName = name || 'this item';
    this.isDeleteDialogOpen = true;
  }

  closeDeleteDialog() {
    console.log('[DEBUG closeDeleteDialog] Closing delete dialog');
    this.deleteItemType = '';
    this.deleteItemId = null;
    this.deleteItemName = '';
    this.isDeleteDialogOpen = false;
  }

  // Methods to close dialogs
  closeFloorDialog() {
    this.floorForm.reset({ name: '', level: '', isActive: true });
    this.isFloorDialogOpen = false;
  }

  closeRoomDialog() {
    this.roomForm.reset({ floorId: '', name: '', type: '' });
    this.isRoomDialogOpen = false;
  }

  closeDeskDialog() {
    this.deskForm.reset({ roomId: '', code: '', isAvailable: true, assignedTo: '' });
    this.isDeskDialogOpen = false;
  }

  // ========== HELPER METHODS FOR TEMPLATE ==========

  // Load all data at component init (parallel calls)
  loadAllData() {
    console.log('[DEBUG loadAllData] Starting to load all data...');
    this.isLoading = true;

    forkJoin({
      floors: this.floorService.list(),
      rooms: this.roomService.list(),
      desks: this.deskService.list()
    }).subscribe({
      next: (result) => {
        console.log('[DEBUG loadAllData] Floors loaded:', result.floors);
        console.log('[DEBUG loadAllData] Floor IDs:', result.floors.map(f => f.id));
        console.log('[DEBUG loadAllData] Rooms loaded:', result.rooms);
        console.log('[DEBUG loadAllData] Desks loaded:', result.desks);

        // Check if rooms have floorId
        if (result.rooms.length > 0) {
          console.log('[DEBUG loadAllData] First room:', result.rooms[0]);
          console.log('[DEBUG loadAllData] First room floorId:', result.rooms[0].floorId);
        }

        this.allFloors = result.floors;

        // Get valid floor IDs for current organization
        const validFloorIds = new Set(result.floors.map(f => f.id));

        // Filter rooms to only include those from current org's floors
        this.allRooms = result.rooms.filter(room => validFloorIds.has(room.floorId));

        // Filter desks to only include those from current org's rooms
        const validRoomIds = new Set(this.allRooms.map(r => r.id));
        this.allDesks = result.desks.filter(desk => validRoomIds.has(desk.roomId));

        this.isLoading = false;
        this.cdr.markForCheck();

        console.log('[DEBUG loadAllData] After filtering - Floors:', this.allFloors.length,
                    'Rooms:', this.allRooms.length, 'Desks:', this.allDesks.length);

        // Auto-select first floor
        if (result.floors.length > 0) {
          this.selectedFloorId = result.floors[0].id;
          console.log('[DEBUG loadAllData] Auto-selected floor:', this.selectedFloorId);
        }
      },
      error: (err) => {
        console.error('[DEBUG loadAllData] Error loading data:', err);
        this.errorMessage = 'Error loading data';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  // Methods to switch tabs
  switchTab(tab: 'floors' | 'rooms' | 'desks') {
    this.activeTab = tab;
  }

  // Methods to check if an entity is selected
  isFloorSelected(floorId: number): boolean {
    return this.selectedFloorId === floorId;
  }

  isRoomSelected(roomId: number): boolean {
    return this.selectedRoomId === roomId;
  }

  isDeskSelected(deskId: number): boolean {
    return this.selectedDeskId === deskId;
  }

  // Methods to get names for display
  getFloorName(floorId: number): string {
    const floor = this.allFloors.find(f => f.id === floorId);
    return floor ? (floor.name || `Floor ${floor.level}`) : 'Unknown Floor';
  }

  getRoomName(roomId: number): string {
    const room = this.allRooms.find(r => r.id === roomId);
    return room ? room.name : 'Unknown Room';
  }

  getDeskName(deskId: number): string {
    const desk = this.allDesks.find(d => d.id === deskId);
    return desk ? desk.name : 'Unknown Desk';
  }

  getRoomCount(floorId: number): number {
    return this.allRooms.filter(r => r.floorId === floorId).length;
  }

  getDeskCount(roomId: number): number {
    return this.allDesks.filter(d => d.roomId === roomId).length;
  }
}
