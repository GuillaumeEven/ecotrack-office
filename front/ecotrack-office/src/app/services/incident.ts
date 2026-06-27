import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IncidentRequest, IncidentResponse } from '../models/incident.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class IncidentService {

  private readonly BASE_URL = `${environment.apiUrl}/incidents`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<IncidentResponse[]> {
    return this.http.get<IncidentResponse[]>(this.BASE_URL);
  }

  create(incident: IncidentRequest): Observable<IncidentResponse> {
    return this.http.post<IncidentResponse>(this.BASE_URL, incident);
  }

  resolve(id: number): Observable<IncidentResponse> {
    return this.http.patch<IncidentResponse>(`${this.BASE_URL}/${id}/resolve`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE_URL}/${id}`);
  }
}