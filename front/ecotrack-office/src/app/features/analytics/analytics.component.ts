import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { AnalyticsService, AuthService, NotificationService } from '@services/index.service';
import { AnalyticsReportResponse } from '@models/index.model';

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
  styleUrls: ['./analytics.component.css'],
  templateUrl: './analytics.component.html',
})
export class AnalyticsComponent implements OnInit {
  allReports: MappedReport[] = [];
  displayedReports: MappedReport[] = [];
  activeFilter: 'day' | 'week' | 'month' | 'all' = 'all';
  today: string = new Date().toISOString().split('T')[0]; // Formato YYYY-MM-DD
  todayReportExists: boolean = false; // Indica si ya existe un informe para hoy
  todayReport: MappedReport | null = null; // Almacena el informe de hoy si existe
  showNoReportMessage: boolean = false; // Indica si debe mostrarse el mensaje "No informe para hoy"

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
    private notificationService: NotificationService,
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
          compiledAt: report.createdAt,
          co2Saved: report.co2SavingsKg ?? 0,
          financialSaved: report.energySavingsEuros ?? 0,
          totalReservations: report.totalReservations ?? 0,
          activeReservationsCount: report.totalReservations ?? 0,
          checkInsCount: report.confirmedCheckIns ?? 0
        }));

        // Ordenar por fecha descendente (el más reciente en posición 0)
        this.allReports.sort((a, b) =>
          new Date(b.compiledAt).getTime() - new Date(a.compiledAt).getTime()
        );

        // check if a report for today already exists
        this.todayReportExists = this.allReports.some(report => report.compiledAt.startsWith(this.today));
        if (this.todayReportExists) {
          this.todayReport = this.allReports.find(report => report.compiledAt.startsWith(this.today)) || null;
        } else {
          this.todayReport = null;
        }

        // Aplico el filtro activo para mostrar los datos correctos en la tabla y recalcular los totales
        this.applyFilter(this.activeFilter);
        this.cdr.markForCheck(); // Marcar componente como changed, sin forzar detección inmediata

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
      // Mostrar reporte de hoy si existe, si no mostrar vacio (con --)
      if (this.todayReportExists && this.todayReport) {
        this.displayedReports = [this.todayReport];
        this.showNoReportMessage = false;
      } else {
        this.displayedReports = [];
        this.showNoReportMessage = true; // Mostrar mensaje "No informe para hoy"
      }
    } else if (filterType === 'week') {
      // this.displayedReports incluye los reports que tienen fecha dentro de los últimos 7 días, incluyendo hoy
      this.displayedReports = this.allReports.filter(report => {
        const reportDate = new Date(report.compiledAt);
        const sevenDaysAgo = new Date(this.today);
        sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 6); // Incluye hoy y los últimos 6 días
        return reportDate >= sevenDaysAgo && reportDate <= new Date(this.today);
      });
      this.showNoReportMessage = false;
    } else if (filterType === 'month') {
      // this.displayedReports incluye los reports que tienen fecha dentro de los últimos 30 días, incluyendo hoy
      this.displayedReports = this.allReports.filter(report => {
        const reportDate = new Date(report.compiledAt);
        const thirtyDaysAgo = new Date(this.today);
        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 29); // Incluye hoy y los últimos 29 días
        return reportDate >= thirtyDaysAgo && reportDate <= new Date(this.today);
      });
      this.showNoReportMessage = false;
    } else {
      this.displayedReports = [...this.allReports];
      this.showNoReportMessage = false;
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

    // Check if a report for today already exists in this.allReports

    if (this.todayReportExists && this.todayReport) {
      // Delete the existing report first
      this.analyticsService.delete(this.todayReport.id).subscribe({
        next: () => {
          // After deletion, create the new report
          this.analyticsService.generate({ dateReport: this.today }).subscribe({
            next: () => {
              this.notificationService.success(`Informe recreado para ${this.todayReport?.compiledAt}`);
              this.loadAnalytics(); // Reload to show new data
            },
            error: (err) => {
              this.notificationService.error('Error al crear el informe después de la eliminación');
            }
          });
        },
        error: (err) => {
          this.notificationService.error('Error al eliminar el informe anterior');
        }
      });
    } else {
      // No report for today, just create a new one
      this.analyticsService.generate({ dateReport: this.today }).subscribe({
        next: () => {
          this.notificationService.success(`Informe creado para ${this.today}`);
          this.loadAnalytics(); // Reload to show new data
        },
        error: (err) => {
          this.notificationService.error('Error al crear el informe');
        }
      });
    }
  }
}