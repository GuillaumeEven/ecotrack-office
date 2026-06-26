import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalyticsReportRequest, AnalyticsReportResponse } from '../models/analytics.model';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = '/api/v1/analytics-reports';

  constructor(private http: HttpClient) {}

  getAll(): Observable<AnalyticsReportResponse[]> {
    return this.http.get<AnalyticsReportResponse[]>(this.apiUrl);
  }

  getById(id: number): Observable<AnalyticsReportResponse> {
    return this.http.get<AnalyticsReportResponse>(`${this.apiUrl}/${id}`);
  }

  create(report: AnalyticsReportRequest): Observable<AnalyticsReportResponse> {
    return this.http.post<AnalyticsReportResponse>(this.apiUrl, report);
  }

  update(id: number, report: AnalyticsReportRequest): Observable<AnalyticsReportResponse> {
    return this.http.put<AnalyticsReportResponse>(`${this.apiUrl}/${id}`, report);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}