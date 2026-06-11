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
    console.log('📡 FloorService.list() calling:', url);
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
   * Fetch floor with all rooms, desks and their status for a specific date
   * Single API call replacing 4+ individual calls
   * 
   * @param id Floor ID
   * @param date Date in format YYYY-MM-DD
   * @returns Observable of FloorWithStatus (includes all rooms, desks, and calculated statuses)
   */
  getFloorWithStatus(id: number, date: string): Observable<FloorWithStatus> {
    const url = `${this.apiUrl}/${id}/status`;
    const params = new HttpParams().set('date', date);
    console.log('📡 FloorService.getFloorWithStatus() calling:', url, 'date:', date);
    return this.httpClient.get<FloorWithStatus>(url, { params });
  }
}
