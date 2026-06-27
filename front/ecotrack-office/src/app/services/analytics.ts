import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalyticsReportRequest, AnalyticsReportResponse } from '../models/analytics.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  private readonly BASE_URL = `${environment.apiUrl}/analytics-reports`;
  // private apiUrl = '/api/v1/analytics-reports';

  constructor(private http: HttpClient) {}

  getAll(): Observable<AnalyticsReportResponse[]> {
    return this.http.get<AnalyticsReportResponse[]>(this.BASE_URL);
  }

  getById(id: number): Observable<AnalyticsReportResponse> {
    return this.http.get<AnalyticsReportResponse>(`${this.BASE_URL}/${id}`);
  }

  create(report: AnalyticsReportRequest): Observable<AnalyticsReportResponse> {
    return this.http.post<AnalyticsReportResponse>(this.BASE_URL, report);
  }

  update(id: number, report: AnalyticsReportRequest): Observable<AnalyticsReportResponse> {
    return this.http.put<AnalyticsReportResponse>(`${this.BASE_URL}/${id}`, report);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE_URL}/${id}`);
  }
}