import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import {
  IncidentService,
  FloorService,
  RoomService,
  DeskService,
  NotificationService,
  AuthService,
  ResourceService
} from '@services/index.service';
import { FloorModel, RoomModel, DeskModel, IncidentResponse } from '@models/index.model';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe],
  styleUrls: ['./incidents.component.css'],
  templateUrl: './incidents.component.html',
})
export class IncidentsComponent implements OnInit {
  incidents: IncidentResponse[] = [];
  incidentForm!: FormGroup;
  isLoading = true;
  isSaving = false;
  userRole: string = 'USER';
  resourceNames: Map<number, string> = new Map();
  protected readonly String = String;

  // Hierarchical resource selection
  floors: FloorModel[] = [];
  rooms: RoomModel[] = [];
  desks: DeskModel[] = [];

  selectedFloorId: number | null = null;
  selectedRoomId: number | null = null;
  selectedDeskId: number | null = null;
  selectedResourceId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private authService: AuthService,
    private resourceService: ResourceService,
    private floorService: FloorService,
    private roomService: RoomService,
    private deskService: DeskService,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.getUserRole();
    this.initForm();
    this.loadFloors();
    this.loadIncidents();
  }

  getUserRole(): void {
    const roleGuardado = this.authService.getRole();
    if (roleGuardado !== null) {
      this.userRole = roleGuardado;
    } else {
      this.userRole = 'USER';
    }
  }

  initForm(): void {
    this.incidentForm = this.fb.group({
      description: ['', [Validators.required, Validators.minLength(10)]],
      userId: ['1', [Validators.required]]
    });
  }

  loadFloors(): void {
    this.floorService.list().subscribe({
      next: (data) => {
        this.floors = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cdr.detectChanges();
      }
    });
  }

  selectFloor(valueStr: string): void {
    const value = valueStr ? Number(valueStr) : null;
    this.onFloorSelected(value);
  }

  selectRoom(valueStr: string): void {
    const value = valueStr ? Number(valueStr) : null;
    this.onRoomSelected(value);
  }

  selectDesk(valueStr: string): void {
    const value = valueStr ? Number(valueStr) : null;
    this.onDeskSelected(value);
  }

  onFloorSelected(floorId: number | null): void {
    this.selectedFloorId = floorId;
    this.selectedRoomId = null;
    this.selectedDeskId = null;
    this.selectedResourceId = null;
    this.rooms = [];
    this.desks = [];

    if (floorId === null) {
      this.cdr.detectChanges();
      return;
    }

    this.roomService.listByFloor(floorId).subscribe({
      next: (data) => {
        this.rooms = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cdr.detectChanges();
      }
    });
  }

  onRoomSelected(roomId: number | null): void {
    this.selectedRoomId = roomId;
    this.selectedDeskId = null;
    this.selectedResourceId = null;
    this.desks = [];

    if (roomId === null) {

      this.cdr.detectChanges();
      return;
    }

    const selectedRoom = this.rooms.find(r => r.id === roomId);

    if (selectedRoom && selectedRoom.roomType === 'MEETING_ROOM') {

      this.selectedResourceId = roomId;
      this.cdr.detectChanges();
      return;
    }

    if (selectedRoom && selectedRoom.roomType === 'DESK_AREA') {
      this.deskService.listByRoom(roomId).subscribe({
        next: (data) => {
          this.desks = data;
          this.cdr.detectChanges();
        },
        error: () => {
          this.notificationService.show('error', 'No se pudieron cargar los escritorios.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  onDeskSelected(deskId: number | null): void {
    this.selectedDeskId = deskId;

    if (deskId === null) {

      this.selectedResourceId = this.selectedRoomId;
    } else {

      this.selectedResourceId = deskId;
    }

    this.cdr.detectChanges();
  }

  onSubmit(): void {
    if (this.incidentForm.invalid || this.selectedResourceId === null) return;
    this.isSaving = true;

    const payload = {
      description: this.incidentForm.value.description,
      resourceId: this.selectedResourceId,
      userId: Number(this.incidentForm.value.userId)
    };

    this.incidentService.create(payload).subscribe({
      next: () => {
        this.isSaving = false;
        this.incidentForm.patchValue({ description: '' });
        this.incidentForm.get('description')?.markAsUntouched();
        this.selectedFloorId = null;
        this.selectedRoomId = null;
        this.selectedDeskId = null;
        this.selectedResourceId = null;
        this.rooms = [];
        this.desks = [];
        this.loadIncidents();
        this.notificationService.show('success', 'Incidencia enviada correctamente.');
      },
      error: () => {
        this.isSaving = false;
        this.notificationService.show('error', 'Error al enviar la incidencia.');
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * MOTOR DE TRADUCCIÓN EXACTO DE LA BASE DE DATOS
   * Traduce los IDs incrementales de MySQL en los nombres normativos del MVP.
   */
  getResourceName(id: number): string {
    return this.resourceNames.get(id) || `Cargando...`;
  }

  private loadResourceName(id: number): void {
    if (this.resourceNames.has(id)) return;

    this.resourceService.getResourceById(id).subscribe({
      next: (data) => {
        this.resourceNames.set(id, data.name);
        this.cdr.markForCheck();
      },
      error: () => {
        this.resourceNames.set(id, `Recurso Externo #${id}`);
        this.cdr.markForCheck();
      }
    });
  }

  loadIncidents(): void {
    this.isLoading = true;
    this.incidentService.getAll().subscribe({
      next: (data) => {
        this.incidents = data;
        data.forEach(incident => this.loadResourceName(incident.resourceId));
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.notificationService.show('error', 'No se pudieron cargar las incidencias desde el servidor.');
        this.cdr.detectChanges();
      }
    });
  }

  onResolve(id: number): void {
    this.incidentService.resolve(id).subscribe({
      next: () => {
        this.loadIncidents();
      },
      error: () => {
        this.notificationService.show('error', 'No se pudo resolver la incidencia.');
        this.cdr.detectChanges();
      }
    });
  }

  onDelete(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta incidencia permanentemente?')) {
      this.incidentService.delete(id).subscribe({
        next: () => {
          this.loadIncidents();
        },
        error: () => {
          this.cdr.detectChanges();
        }
      });
    }
  }
}