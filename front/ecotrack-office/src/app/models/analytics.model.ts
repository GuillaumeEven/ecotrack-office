export interface AnalyticsReportRequest {
  dateReport: string; // Format: YYYY-MM-DD
}

export interface AnalyticsReportResponse {
  id: number;
  co2SavingsKg: number;
  energySavingsEuros: number;
  totalReservations: number;
  confirmedCheckIns: number;
  emptyRooms: number;
  generatedAt: string;
  organizationId: number;
}