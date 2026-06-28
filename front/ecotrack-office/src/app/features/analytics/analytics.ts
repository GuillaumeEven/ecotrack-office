import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { AnalyticsService } from '../../services/analytics';
import { AuthService } from '../../services/auth.service';
import { AnalyticsReportResponse } from '../../models/analytics.model';

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
  imports: [CommonModule, DecimalPipe],
  templateUrl: './analytics.html',
})
export class AnalyticsComponent implements OnInit {
  allReports: MappedReport[] = [];
  displayedReports: MappedReport[] = [];
  activeFilter: 'day' | 'week' | 'month' | 'all' = 'all';

  // Totales dinámicos que se muestran en las tarjetas de arriba
  accumulatedTotals = {
    co2Saved: 0,
    financialSaved: 0,
    activeReservationsCount: 0,
    checkInsCount: 0
  };

  // Equivalencias basadas en el filtro activo
  equivalences = {
    treesPlanted: 0,
    officeShutdownDays: 0
  };

  isLoading = true;
  errorMessage: string | null = null;
  userRole: string = 'USER';

  constructor(
    private analyticsService: AnalyticsService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.getUserRole();
    this.loadAnalytics();
  }

  getUserRole(): void {
    const roleGuardado = this.authService.getRole();
    if (roleGuardado !== null) {
      this.userRole = roleGuardado;
    } else {
      this.userRole = 'USER';
    }
  }

  loadAnalytics(): void {
    this.isLoading = true;
    this.analyticsService.getAll().subscribe({
      next: (data: AnalyticsReportResponse[]) => {
        this.allReports = data.map((report: any) => ({
          id: report.id,
          compiledAt: report.generatedAt || report.generated_at || report.createdAt || report.created_at,
          co2Saved: report.co2SavingsKg ?? report.co2_savings_kg ?? report.co_2_savings_kg ?? 0,
          financialSaved: report.energySavingsEuros ?? report.energy_savings_euros ?? 0,
          totalReservations: report.totalReservations ?? report.total_reservations ?? 0, // Campo nativo guardado por si acaso
          activeReservationsCount: report.totalReservations ?? report.total_reservations ?? 0,
          checkInsCount: report.confirmedCheckIns ?? report.confirmed_check_ins ?? 0
        }));

        // Ordenar por fecha descendente (el más reciente en posición 0)
        this.allReports.sort((a, b) =>
          new Date(b.compiledAt).getTime() - new Date(a.compiledAt).getTime()
        );


        // const today = new Date().toISOString().split('T')[0]; // Formato YYYY-MM-DD
        // const todayReportExists = this.allReports.some(report => report.compiledAt.startsWith(today));
        // if (!todayReportExists) {
        //   this.compileMetrics(today);
        // }
        // this.allReports.sort((a, b) => b.id - a.id);

        // Aplico el filtro activo para mostrar los datos correctos en la tabla y recalcular los totales
        this.applyFilter(this.activeFilter);
        this.cdr.detectChanges(); // Forzar la detección de cambios para actualizar la vista

        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar las métricas de sostenibilidad.';
        this.isLoading = false;
      }
    });
  }

  applyFilter(filterType: 'day' | 'week' | 'month' | 'all'): void {
    this.activeFilter = filterType;

    // 1. Filtrar el historial de abajo por cantidad de informes diarios
    if (filterType === 'day') {
      // encuentro si hay un informe para hoy y lo muestro, si no, muestro el más reciente
      const today = new Date().toISOString().split('T')[0]; // Formato YYYY-MM-DD
      const todayReport = this.allReports.find(report => report.compiledAt.startsWith(today));
      if (todayReport) {
        this.displayedReports = [todayReport];
      } else {
        //crear un nuevo informe con id 0
        const newReport = {
          id: 0,
          compiledAt: today,
          co2Saved: 0,
          financialSaved: 0,
          totalReservations: 0,
          activeReservationsCount: 0,
          checkInsCount: 0
        };
        this.displayedReports = [newReport];
        // this.displayedReports = this.allReports.slice(0, 1); // Mostrar el informe más reciente si no hay uno para hoy
      }

      // this.displayedReports = this.allReports.slice(0, 1);
    } else if (filterType === 'week') {
      this.displayedReports = this.allReports.slice(0, 7);
    } else if (filterType === 'month') {
      this.displayedReports = this.allReports.slice(0, 30);
    } else {
      this.displayedReports = [...this.allReports];
    }

    // 2. Recalcular las tarjetas de arriba usando SOLO los datos visibles del filtro
    this.calculateTotalsForDisplayed();
  }

  calculateTotalsForDisplayed(): void {
    this.accumulatedTotals = this.displayedReports.reduce((acc, report) => {
      acc.co2Saved += report.co2Saved;
      acc.financialSaved += report.financialSaved;
      acc.activeReservationsCount += report.activeReservationsCount;
      acc.checkInsCount += report.checkInsCount;
      return acc;
    }, { co2Saved: 0, financialSaved: 0, activeReservationsCount: 0, checkInsCount: 0 });

    // Cifras de equivalencia conservadoras y realistas basadas en el filtro seleccionado
    this.equivalences.treesPlanted = Math.floor(this.accumulatedTotals.co2Saved / 20);
    this.equivalences.officeShutdownDays = Math.floor(this.accumulatedTotals.financialSaved / 40); // 40€ de coste diario estimado de una oficina pequeña
  }

  compileMetrics(): void {
    const today = new Date().toISOString().split('T')[0]; // Format: YYYY-MM-DD

    // Check if a report for today already exists in this.allReports
    const existingTodayReport = this.allReports.find(report =>
      report.compiledAt.startsWith(today)
    );

    if (existingTodayReport) {
      // Delete the existing report first
      this.analyticsService.delete(existingTodayReport.id).subscribe({
        next: () => {
          // After deletion, create the new report
          this.analyticsService.generate({ dateReport: today }).subscribe({
            next: () => {
              console.log('Report replaced for', today);
              this.loadAnalytics(); // Reload to reflect changes
            },
            error: (err) => {
              console.error('Error creating new report:', err);
            }
          });
        },
        error: (err) => {
          console.error('Error deleting old report:', err);
        }
      });
    } else {
      // No report for today, just create a new one
      this.analyticsService.generate({ dateReport: today }).subscribe({
        next: () => {
          console.log('Report created for', today);
          this.loadAnalytics(); // Reload to show new data
        },
        error: (err) => {
          console.error('Error creating report:', err);
        }
      });
    }
  }
}