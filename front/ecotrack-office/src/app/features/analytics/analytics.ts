import { Component, OnInit } from '@angular/core';
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
    private authService: AuthService
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

        this.allReports.sort((a, b) => b.id - a.id);

        // Aplico el filtro activo para mostrar los datos correctos en la tabla y recalcular los totales
        this.applyFilter(this.activeFilter);

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
      this.displayedReports = this.allReports.slice(0, 1);
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
    // 1. Simulamos las salas cerradas de 1 a 3 (eliminamos el 0 para que siempre muestre impacto)
    const emptyRooms = Math.floor(Math.random() * 3) + 1; // Genera 1, 2 o 3

    // 2. Aplicamos la escala de costes reales según el tipo de sala cerrada
    let co2 = 0;
    let energy = 0;

    if (emptyRooms === 1) {
      co2 = 1.50;
      energy = 5.00;
    } else if (emptyRooms === 2) {
      co2 = 2.30;
      energy = 8.00;
    } else if (emptyRooms === 3) {
      co2 = 3.80;
      energy = 13.00;
    }

    // 3. Ocupación realista con la escala de puestos de trabajo y reservas diarias, para que no se generen métricas irreales
    const reservations = Math.floor(Math.random() * 9) + 28; // Entre 28 y 36 reservas diarias
    const checkins = Math.floor(Math.random() * 7) + 24;     // Entre 24 y 30 check-ins reales

    // 4. Empaqueto el DTO respetando ambas nomenclaturas para el backend
    const payload = {
      co2SavingsKg: co2,
      co2_savings_kg: co2,
      energySavingsEuros: energy,
      energy_savings_euros: energy,
      totalReservations: reservations,
      total_reservations: reservations,
      confirmedCheckIns: checkins,
      confirmed_check_ins: checkins,
      emptyRooms: emptyRooms,
      empty_rooms: emptyRooms,
      organizationId: 1,
      organization_id: 1
    };

    this.analyticsService.create(payload as any).subscribe({
      next: () => {
        this.loadAnalytics(); // Recarga todo el histórico de MySQL y recalcula totales
      },
      error: () => {
        this.errorMessage = 'Error al compilar la actualización de métricas.';
      }
    });
  }
}