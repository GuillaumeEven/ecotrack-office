import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Desk } from '../models';
import { API_CONFIG } from './api.config';

/**
 * Desk Service
 * Handles all HTTP operations for Desk entities
 */
@Injectable({
  providedIn: 'root'
})
export class DeskService {
  private apiUrl = API_CONFIG.baseUrl + API_CONFIG.endpoints.desks;

  constructor(private httpClient: HttpClient) {}

  /**
   * Fetch all desks
   */
  list(): Observable<Desk[]> {
    return this.httpClient.get<Desk[]>(this.apiUrl);
  }

  /**
   * Fetch all desks in a specific room
   */
  listByRoom(roomId: number): Observable<Desk[]> {
    return this.httpClient.get<Desk[]>(`${this.apiUrl}/room/${roomId}`);
  }

  /**
   * Fetch a single desk by ID
   */
  get(id: number): Observable<Desk> {
    return this.httpClient.get<Desk>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new desk
   */
  create(desk: Omit<Desk, 'id'>): Observable<Desk> {
    return this.httpClient.post<Desk>(this.apiUrl, desk);
  }

  /**
   * Update an existing desk
   */
  update(id: number, desk: Partial<Desk>): Observable<Desk> {
    return this.httpClient.put<Desk>(`${this.apiUrl}/${id}`, desk);
  }

  /**
   * Delete a desk
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/${id}`);
  }
}
