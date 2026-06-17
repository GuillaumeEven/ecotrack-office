import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IncidentRequest, IncidentResponse } from '../models/incident.model';

@Injectable({
  providedIn: 'root'
})
export class IncidentService {
  private apiUrl = '/api/v1/incidents'; 

  constructor(private http: HttpClient) {}

  getAll(): Observable<IncidentResponse[]> {
    return this.http.get<IncidentResponse[]>(this.apiUrl);
  }

  create(incident: IncidentRequest): Observable<IncidentResponse> {
    return this.http.post<IncidentResponse>(this.apiUrl, incident);
  }

  resolve(id: number): Observable<IncidentResponse> {
    return this.http.patch<IncidentResponse>(`${this.apiUrl}/${id}/resolve`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}