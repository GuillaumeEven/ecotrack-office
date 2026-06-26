import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IncidentService } from '../../services/incident';
import { AuthService } from '../../services/auth.service';
import { IncidentResponse } from '../../models/incident.model';
import { ResourceService } from '../../services/resource.service';

@Component({
  selector: 'app-incidencias',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe],
  templateUrl: './incidencias.html',
})
export class IncidenciasComponent implements OnInit {
  incidents: IncidentResponse[] = [];
  incidentForm!: FormGroup;
  isLoading = true;
  isSaving = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  userRole: string = 'USER';
  resourceNames: Map<number, string> = new Map();
  protected readonly String = String;

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private authService: AuthService,
    private resourceService: ResourceService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.getUserRole();
    this.initForm();
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
      resourceId: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      userId: ['1', [Validators.required]]
    });
  }

  /**
   * 🔄 MOTOR DE TRADUCCIÓN EXACTO DE LA BASE DE DATOS
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
        this.cdr.markForCheck();  // ← Force Angular à redessiner
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
        this.errorMessage = 'No se pudieron cargar las incidencias desde el servidor.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit(): void {
    if (this.incidentForm.invalid) return;
    this.isSaving = true;
    this.successMessage = null;
    this.errorMessage = null;

    const payload = {
      description: this.incidentForm.value.description,
      resourceId: Number(this.incidentForm.value.resourceId),
      userId: Number(this.incidentForm.value.userId)
    };

    this.incidentService.create(payload).subscribe({
      next: () => {
        this.isSaving = false;
        this.successMessage = 'Incidencia reportada con éxito.';
        this.incidentForm.patchValue({ description: '', resourceId: '' });
        this.incidentForm.get('description')?.markAsUntouched();
        this.incidentForm.get('resourceId')?.markAsUntouched();
        this.loadIncidents();
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Error al enviar la incidencia.';
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
        this.errorMessage = 'No se pudo resolver la incidencia.';
        this.cdr.detectChanges();
      }
    });
  }

  onDelete(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta incidencia permanentemente?')) {
      this.incidentService.delete(id).subscribe({
        next: () => {
          this.successMessage = 'Incidencia eliminada correctamente.';
          this.loadIncidents();
        },
        error: () => {
          this.errorMessage = 'No se pudo eliminar la incidencia.';
          this.cdr.detectChanges();
        }
      });
    }
  }
}