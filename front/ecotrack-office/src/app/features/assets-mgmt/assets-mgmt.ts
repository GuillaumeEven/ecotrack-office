import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Desk, Floor, ResourceStatus, Room } from '../../building-map/models';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { FloorService } from '../../building-map/services/floor.service';
import { RoomService } from '../../building-map/services/room.service';
import { DeskService } from '../../building-map/services/desk.service';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NotificationService } from '../../services/notification.service'; // 🆕

@Component({
  selector: 'app-assets-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './assets-mgmt.html',
  styleUrls: ['./assets-mgmt.css'],
})
export class AssetsMgmt implements OnInit {
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

  // Tab labels mapping for Spanish display
  tabLabels: Record<'floors' | 'rooms' | 'desks', string> = {
    floors: 'Pisos',
    rooms: 'Salas',
    desks: 'Escritorios',
  };

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
  deleteItemSummary: string = ''; // 🆕

  // Loading and error state
  isLoading = false;
  errorMessage: string | null = null;

  // Incidents cache (loaded once at init)
  incidentsByDeskId: Map<number, any[]> = new Map();
  incidentsByRoomId: Map<number, any[]> = new Map();

  // Current user's organization ID
  organizationId: number | null = null;

  constructor(
    private floorService: FloorService,
    private roomService: RoomService,
    private deskService: DeskService,
    private userService: UserService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService // 🆕
  ) {
    this.initializeForms();
  }

  ngOnInit(): void {
    // Wait for user authentication before loading data
    // This ensures we have organizationId before making API calls
    this.userService.getMe().subscribe({
      next: (user) => {
        this.organizationId = user.organizationId;
        this.loadAllData();
      },
      error: (err) => {
        this.errorMessage = 'Error loading user information';
        this.cdr.markForCheck();
      }
    });
  }

  // ========== GETTERS FOR FILTERED DATA ==========

  get floors(): Floor[] {
    return this.allFloors;
  }

  get filteredRooms(): Room[] {
    if (!this.selectedFloorId) {
      return this.allRooms; // Show all if no floor selected
    }

    const filtered = this.allRooms.filter((room) => {
      return room.floorId === this.selectedFloorId;
    });
    return filtered;
  }

  get filteredDesks(): Desk[] {
    if (!this.selectedRoomId) {
      return this.allDesks; // Show all if no room selected
    }
    return this.allDesks.filter((desk) => desk.roomId === this.selectedRoomId);
  }

  get deskAreas(): Room[] {
    // Only return rooms with roomType DESK_AREA
    return this.allRooms.filter((room) => room.roomType === 'DESK_AREA');
  }

  // ========== TAB MANAGEMENT ==========

  setActiveTab(tab: 'floors' | 'rooms' | 'desks') {
    this.activeTab = tab;
    // No need to fetch data - already loaded at init
  }

  getTabLabel(tab: 'floors' | 'rooms' | 'desks'): string {
    return this.tabLabels[tab];
  }

  // ========== FORM INITIALIZATION ==========

  initializeForms() {
    this.floorForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      level: ['', [Validators.required, Validators.min(0)]],
      isActive: [true],
    });

    this.roomForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      floorId: ['', Validators.required],
      roomType: ['', Validators.required],
      capacity: ['', [Validators.required, Validators.min(1)]],
      surfaceArea: ['', [Validators.required, Validators.min(0.01)]],
      equipmentList: [''],
      isActive: [true],
    });

    this.deskForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      roomId: ['', Validators.required],
      equipmentList: [''],
      isActive: [true],
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
          const idx = this.allFloors.findIndex((f) => f.id === updated.id);
          if (idx !== -1) this.allFloors[idx] = updated;
          this.closeFloorDialog();
        },
        error: (err) => (this.errorMessage = 'Error updating floor'),
      });
    } else {
      // Create
      this.floorService.create(floorData).subscribe({
        next: (created) => {
          this.allFloors.push(created);
          this.closeFloorDialog();
        },
        error: (err) => (this.errorMessage = 'Error creating floor'),
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
          const idx = this.allRooms.findIndex((r) => r.id === updated.id);
          if (idx !== -1) this.allRooms[idx] = updated;
          this.closeRoomDialog();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = 'Error updating room';
          this.cdr.markForCheck();
        },
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
        },
      });
    }
  }

  submitDesk() {
    if (this.deskForm.invalid) return;

    // Use getRawValue() to include disabled fields
    const deskData = this.deskForm.getRawValue();

    if (this.editingDesk) {
      this.deskService.update(this.editingDesk.id, deskData).subscribe({
        next: (updated) => {
          const idx = this.allDesks.findIndex((d) => d.id === updated.id);
          if (idx !== -1) this.allDesks[idx] = updated;
          this.closeDeskDialog();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = 'Error updating desk';
          this.cdr.markForCheck();
        },
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
        },
      });
    }
  }

  // 🆕 Abre el modal de confirmación con resumen de elementos hijos afectados
  openDeleteDialog(type: 'floor' | 'room' | 'desk', id: number, name?: string) {
    this.deleteItemType = type == 'floor' ? 'piso' : type == 'room' ? 'sala' : 'escritorio';
    this.deleteItemId = id;
    this.deleteItemName = name || 'este elemento';
    this.isDeleteDialogOpen = true;

    if (type === 'floor') {
      const rooms = this.allRooms.filter((r) => r.floorId === id);
      const desks = this.allDesks.filter((d) => rooms.some((r) => r.id === d.roomId));
      this.deleteItemSummary =
        rooms.length > 0
          ? `Este piso contiene ${rooms.length} sala(s) y ${desks.length} escritorio(s) que también serán eliminadas.`
          : '';
    } else if (type === 'room') {
      const desks = this.allDesks.filter((d) => d.roomId === id);
      this.deleteItemSummary =
        desks.length > 0
          ? `Esta sala contiene ${desks.length} escritorio(s) que también serán eliminadas.`
          : '';
    } else {
      this.deleteItemSummary = '';
    }
  }

  // 🆕 Confirma el borrado, limpia el estado local y muestra toast
  confirmDelete() {
    if (this.deleteItemId === null) return;

    switch (this.deleteItemType) {
      case 'floor':
        this.floorService.delete(this.deleteItemId).subscribe({
          next: () => {
            const deletedRoomIds = this.allRooms
              .filter((r) => r.floorId === this.deleteItemId)
              .map((r) => r.id);
            this.allDesks = this.allDesks.filter((d) => !deletedRoomIds.includes(d.roomId));
            this.allRooms = this.allRooms.filter((r) => r.floorId !== this.deleteItemId);
            this.allFloors = this.allFloors.filter((f) => f.id !== this.deleteItemId);
            this.notificationService.success('Piso eliminado correctamente.');
            this.closeDeleteDialog();
            this.cdr.markForCheck();
          },
          error: () => {
            this.notificationService.error('Error al eliminar el piso.');
            this.cdr.markForCheck();
          },
        });
        break;
      case 'room':
        this.roomService.delete(this.deleteItemId).subscribe({
          next: () => {
            this.allDesks = this.allDesks.filter((d) => d.roomId !== this.deleteItemId);
            this.allRooms = this.allRooms.filter((r) => r.id !== this.deleteItemId);
            this.notificationService.success('Sala eliminada correctamente.');
            this.closeDeleteDialog();
            this.cdr.markForCheck();
          },
          error: () => {
            this.notificationService.error('Error al eliminar la sala.');
            this.cdr.markForCheck();
          },
        });
        break;
      case 'desk':
        this.deskService.delete(this.deleteItemId).subscribe({
          next: () => {
            this.allDesks = this.allDesks.filter((d) => d.id !== this.deleteItemId);
            this.notificationService.success('Escritorio eliminada correctamente.');
            this.closeDeleteDialog();
            this.cdr.markForCheck();
          },
          error: () => {
            this.notificationService.error('Error al eliminar la escritorio.');
            this.cdr.markForCheck();
          },
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
    return this.allFloors.find((floor) => floor.id === this.selectedFloorId);
  }

  getSelectedRoom(): Room | undefined {
    return this.allRooms.find((room) => room.id === this.selectedRoomId);
  }

  getSelectedDesk(): Desk | undefined {
    return this.allDesks.find((desk) => desk.id === this.selectedDeskId);
  }

  // Methods to open dialogs
  openFloorDialog(floor?: Floor | null) {
    this.editingFloor = floor || null;
    if (floor) {
      this.floorForm.patchValue({
        name: floor.name,
        level: floor.level,
        isActive: floor.isActive,
      });
    } else {
      this.floorForm.reset({
        name: '',
        level: '',
        isActive: true,
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
        isActive: room.isActive,
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
        isActive: true,
      });
      // Enable floor selection when creating new room
      this.roomForm.get('floorId')?.enable();
    }
    this.isRoomDialogOpen = true;
  }

  openDeskDialog(desk?: Desk | null) {
    this.editingDesk = desk || null;
    if (desk) {
      this.deskForm.patchValue({
        name: desk.name,
        roomId: desk.roomId,
        equipmentList: desk.equipmentList || '',
        isActive: desk.isActive,
      });
      // Disable room selection when editing (room is immutable)
      this.deskForm.get('roomId')?.disable();
    } else {
      this.deskForm.reset({
        name: '',
        roomId: '',
        equipmentList: '',
        isActive: true,
      });
      // Enable room selection when creating new desk
      this.deskForm.get('roomId')?.enable();
    }
    this.isDeskDialogOpen = true;
  }

  // 🆕 closeDeleteDialog limpia también el summary
  closeDeleteDialog() {
    this.deleteItemType = '';
    this.deleteItemId = null;
    this.deleteItemName = '';
    this.deleteItemSummary = '';
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
    this.deskForm.reset({ name: '', roomId: '', equipmentList: '', isActive: true });
    this.isDeskDialogOpen = false;
  }

  // ========== HELPER METHODS FOR TEMPLATE ==========

  // Load all data at component init (parallel calls)
  loadAllData() {
    if (!this.organizationId) {
      this.errorMessage = 'Organization ID not available';
      this.cdr.markForCheck();
      return;
    }

    this.isLoading = true;

    forkJoin({
      floors: this.floorService.list(this.organizationId),
      rooms: this.roomService.list(),
      desks: this.deskService.list(),
    }).subscribe({
      next: (result) => {
        this.allFloors = result.floors;

        // Get valid floor IDs for current organization
        const validFloorIds = new Set(result.floors.map((f) => f.id));

        // Filter rooms to only include those from current org's floors
        this.allRooms = result.rooms.filter((room) => validFloorIds.has(room.floorId));

        // Filter desks to only include those from current org's rooms
        const validRoomIds = new Set(this.allRooms.map((r) => r.id));
        this.allDesks = result.desks.filter((desk) => validRoomIds.has(desk.roomId));

        // Load incidents for all desks (parallel calls)
        this.loadIncidentsForAllDesks();

        this.isLoading = false;
        this.cdr.markForCheck();

        // Auto-select first floor
        if (result.floors.length > 0) {
          this.selectedFloorId = result.floors[0].id;
        }
      },
      error: (err) => {
        this.errorMessage = 'Error loading data';
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  // Load incidents for each desk (run once at init)
  private loadIncidentsForAllDesks() {
    if (this.allDesks.length === 0) {
      return; // No desks to load incidents for
    }

    // Build forkJoin with incidents calls for all desks
    const incidentsRequests: Record<string, any> = {};
    this.allDesks.forEach((desk) => {
      incidentsRequests[`desk_${desk.id}`] = this.deskService
        .getIncidentsByDeskId(desk.id)
        .pipe(
          catchError(() => of([])) // Silently handle errors, default to empty array
        );
    });

    forkJoin(incidentsRequests).subscribe({
      next: (results) => {
        // Map incidents back to desks
        Object.entries(results).forEach(([key, incidents]) => {
          const deskId = parseInt(key.split('_')[1], 10);
          this.incidentsByDeskId.set(deskId, incidents as any[]);
        });
        this.cdr.markForCheck();
      },
      error: () => {
        // If bulk load fails, incidents won't be available but app still works
        console.warn('Failed to load incidents for desks');
      },
    });
  }

  // Load incidents for all rooms (used for room availability check)
  private loadIncidentsForAllRooms() {
    if (this.allRooms.length === 0) {
      return; // No rooms to load incidents for
    }

    // Build forkJoin with incidents calls for all rooms
    const incidentsRequests: Record<string, any> = {};
    this.allRooms.forEach((room) => {
      incidentsRequests[`room_${room.id}`] = this.roomService
        .getIncidentsByRoomId(room.id)
        .pipe(
          catchError(() => of([])) // Silently handle errors, default to empty array
        );
    });

    forkJoin(incidentsRequests).subscribe({
      next: (results) => {
        // Map incidents back to rooms
        Object.entries(results).forEach(([key, incidents]) => {
          const roomId = parseInt(key.split('_')[1], 10);
          this.incidentsByDeskId.set(roomId, incidents as any[]);
        });
        this.cdr.markForCheck();
      },
      error: () => {
        // If bulk load fails, incidents won't be available but app still works
        console.warn('Failed to load incidents for rooms');
      },
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
    const floor = this.allFloors.find((f) => f.id === floorId);
    return floor ? floor.name || `Floor ${floor.level}` : 'Unknown Floor';
  }

  getRoomName(roomId: number): string {
    const room = this.allRooms.find((r) => r.id === roomId);
    return room ? room.name : 'Unknown Room';
  }

  getDeskName(deskId: number): string {
    const desk = this.allDesks.find((d) => d.id === deskId);
    return desk ? desk.name : 'Unknown Desk';
  }

  getRoomCount(floorId: number): number {
    return this.allRooms.filter((r) => r.floorId === floorId).length;
  }

  getDeskCount(roomId: number): number {
    return this.allDesks.filter((d) => d.roomId === roomId).length;
  }

  getDeskAvailability(deskId: number): boolean {
    const incidents = this.incidentsByDeskId.get(deskId) || [];
    console.log(`Desk ID: ${deskId}, Incidents: ${incidents.length}`);
    return incidents.length === 0; // If there are no incidents, desk is available
  }

  getRoomAvailability(roomId: number): boolean {
    const incidents = this.incidentsByRoomId.get(roomId) || [];
    return incidents.length === 0; // If there are no incidents, room is available
  }
}
