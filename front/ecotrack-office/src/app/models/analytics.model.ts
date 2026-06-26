export interface AnalyticsReportRequest {
  co2SavingsKg: number;
  energySavingsEuros: number;
  totalReservations: number;
  confirmedCheckIns: number;
  emptyRooms: number;
  organizationId: number;
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