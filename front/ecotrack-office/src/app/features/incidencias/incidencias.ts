import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IncidentService } from '../../services/incident';
import { AuthService } from '../../services/auth.service'; // Importo el servicio común de auth
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
  userRole: string = 'USER'; // Rol por defecto

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentService,
    private authService: AuthService // Inyecto el control de acceso
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
      userId: ['1', [Validators.required]] // ID temporal para el MVP
    });
  }

  loadIncidents(): void {
    this.isLoading = true;
    this.incidentService.getAll().subscribe({
      next: (data: IncidentResponse[]) => {
        this.incidents = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar las incidencias desde el servidor.';
        this.isLoading = false;
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
      }
    });
  }
}