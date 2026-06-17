import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Floor, FloorWithStatus } from '../models';
import { API_CONFIG } from './api.config';

/**
 * Floor Service
 * Handles all HTTP operations for Floor entities
 */
@Injectable({
  providedIn: 'root'
})
export class FloorService {
  private apiUrl = API_CONFIG.baseUrl + API_CONFIG.endpoints.floors;

  constructor(private httpClient: HttpClient) {}

  /**
   * Fetch all floors for an organization
   */
  list(organizationId: number = API_CONFIG.organizationId): Observable<Floor[]> {
    const url = `${this.apiUrl}/organization/${organizationId}`;
    return this.httpClient.get<Floor[]>(url);
  }

  /**
   * Fetch a single floor by ID
   */
  get(id: number): Observable<Floor> {
    return this.httpClient.get<Floor>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new floor
   */
  create(floor: Omit<Floor, 'id'>): Observable<Floor> {
    return this.httpClient.post<Floor>(this.apiUrl, floor);
  }

  /**
   * Update an existing floor
   */
  update(id: number, floor: Partial<Floor>): Observable<Floor> {
    return this.httpClient.put<Floor>(`${this.apiUrl}/${id}`, floor);
  }

  /**
   * Delete a floor
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Fetch all floors with their rooms, desks and status for a specific date
   * Global progressive unlock logic applied across all floors
   *
   * @param organizationId Organization ID
   * @param date Date in format YYYY-MM-DD
   * @returns Observable of FloorWithStatus[] (all floors with global progressive logic applied)
   */
  getFloorsWithStatus(organizationId: number = API_CONFIG.organizationId, date: string): Observable<FloorWithStatus[]> {
    const url = `${this.apiUrl}/status/${organizationId}`;
    const params = new HttpParams().set('date', date);
    return this.httpClient.get<FloorWithStatus[]>(url, { params });
  }
}
