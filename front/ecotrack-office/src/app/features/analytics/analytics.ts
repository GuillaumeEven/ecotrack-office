import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { AnalyticsService } from '../../services/analytics'; 
import { AuthService } from '../../services/auth.service'; // Importo el servicio común de auth
import { AnalyticsReportResponse } from '../../models/analytics.model';

// Interfaz adaptada para que encaje bien con las variables del HTML
interface MappedReport {
  id: number;
  compiledAt: string;
  co2Saved: number;
  financialSaved: number;
  activeReservationsCount: number;
  checkInsCount: number;
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DecimalPipe],
  templateUrl: './analytics.html',
})
export class AnalyticsComponent implements OnInit {
  reports: MappedReport[] = [];
  metrics: MappedReport | null = null;
  isLoading = true;
  errorMessage: string | null = null;
  userRole: string = 'USER'; // Rol por defecto para proteger la vista

  constructor(
    private analyticsService: AnalyticsService,
    private authService: AuthService // Inyecto el control de acceso
  ) {}

  ngOnInit(): void {
    this.getUserRole();
    this.loadAnalytics();
  }

  getUserRole(): void {
    const roleGuardado = this.authService.getRole();
    
    // Solución definitiva: Valido que no sea null antes de asignarlo
    if (roleGuardado !== null) {
      this.userRole = roleGuardado;
    } else {
      this.userRole = 'USER'; // Si no hay sesión, se queda como usuario básico
    }
  }

  loadAnalytics(): void {
    this.isLoading = true;
    this.analyticsService.getAll().subscribe({
      next: (data: AnalyticsReportResponse[]) => {
        // Adapto los nombres de variables para que encajen con la interfaz MappedReport
        this.reports = data.map(report => ({
          id: report.id,
          compiledAt: report.generatedAt,
          co2Saved: report.co2SavingsKg,
          financialSaved: report.energySavingsEuros,
          activeReservationsCount: report.totalReservations,
          checkInsCount: report.confirmedCheckIns
        }));

        if (this.reports.length > 0) {
          // Ordeno para dejar el más reciente arriba
          this.reports.sort((a, b) => b.id - a.id);
          this.metrics = this.reports[0];
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar las métricas de sostenibilidad.';
        this.isLoading = false;
      }
    });
  }

  // Cambio de nombre de función para reflejar mejor su propósito: "compilarMetrics" -> "compileMetrics"
  compileMetrics(): void {
    const mockPayload = {
      co2SavingsKg: Math.random() * 50 + 10,
      energySavingsEuros: Math.random() * 120 + 30,
      totalReservations: Math.floor(Math.random() * 40) + 10,
      confirmedCheckIns: Math.floor(Math.random() * 10) + 5,
      emptyRooms: Math.floor(Math.random() * 3),
      organizationId: 1
    };

    this.analyticsService.create(mockPayload).subscribe({
      next: () => {
        this.loadAnalytics();
      },
      error: () => {
        this.errorMessage = 'Error al compilar la actualización de métricas.';
      }
    });
  }
}