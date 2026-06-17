import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { AnalyticsService } from '../../services/analytics'; // Ruta ajustada al archivo real
import { AnalyticsReportResponse } from '../../models/analytics.model';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DecimalPipe],
  templateUrl: './analytics.html',
})
export class AnalyticsComponent implements OnInit {
  reports: AnalyticsReportResponse[] = [];
  latestReport: AnalyticsReportResponse | null = null;
  isLoading = true;
  errorMessage: string | null = null;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoading = true;
    this.analyticsService.getAll().subscribe({
      next: (data: AnalyticsReportResponse[]) => {
        this.reports = data;
        if (data.length > 0) {
          // Ordeno por ID descendente para tener el más reciente primero
          this.reports.sort((a, b) => b.id - a.id);
          this.latestReport = this.reports[0];
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load sustainability metrics.';
        this.isLoading = false;
      }
    });
  }

  // Función extra para simular la generación de un nuevo informe de sostenibilidad
  onGenerateReport(): void {
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
        this.errorMessage = 'Failed to trigger new data compilation.';
      }
    });
  }
}