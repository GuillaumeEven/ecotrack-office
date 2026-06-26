import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IncidentService } from '../../services/incident';
import { AuthService } from '../../services/auth.service'; 
import { IncidentResponse } from '../../models/incident.model';

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
  protected readonly String = String;

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private authService: AuthService,
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
    // PLANTA 1
    if (id === 1) return 'Sala de Trabajo A';
    if (id >= 2 && id <= 11) return `Escritorio D${id - 1} (Planta 1 - Sala A)`;

    if (id === 12) return 'Sala de Trabajo B';
    if (id >= 13 && id <= 22) return `Escritorio D${id - 12} (Planta 1 - Sala B)`;

    if (id === 23) return 'Sala de Trabajo C';
    if (id >= 24 && id <= 33) return `Escritorio D${id - 23} (Planta 1 - Sala C)`;

    if (id === 34) return 'Sala de Reunión 1 (Planta 1)';
    if (id === 35) return 'Sala de Reunión 2 (Planta 1)';

    // PLANTA 2
    if (id === 36) return 'Sala de Trabajo D';
    if (id >= 37 && id <= 46) return `Escritorio D${id - 36} (Planta 2 - Sala D)`;

    if (id === 47) return 'Sala de Trabajo E';
    if (id >= 48 && id <= 57) return `Escritorio D${id - 47} (Planta 2 - Sala E)`;

    if (id === 58) return 'Sala de Reunión 3 (Planta 2)';
    if (id === 59) return 'Sala de Reunión 4 (Planta 2)';

    return `Recurso Externo #${id}`;
  }

  loadIncidents(): void {
    this.isLoading = true;
    this.incidentService.getAll().subscribe({
      next: (data: IncidentResponse[]) => {
        this.incidents = data;
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