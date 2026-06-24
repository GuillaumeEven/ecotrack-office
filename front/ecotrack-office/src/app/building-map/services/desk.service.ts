import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Desk } from '../models';
import { environment } from '../../../environments/environment';
/**
 * Desk Service
 * Handles all HTTP operations for Desk entities
 */
@Injectable({
  providedIn: 'root'
})
export class DeskService {
  private readonly BASE_URL = `${environment.apiUrl}/desks`;

  constructor(private httpClient: HttpClient) {}

  /**
   * Fetch all desks
   */
  list(): Observable<Desk[]> {
    return this.httpClient.get<Desk[]>(this.BASE_URL);
  }

  /**
   * Fetch all desks in a specific room
   */
  listByRoom(roomId: number): Observable<Desk[]> {
    return this.httpClient.get<Desk[]>(`${this.BASE_URL}/room/${roomId}`);
  }

  /**
   * Fetch a single desk by ID
   */
  get(id: number): Observable<Desk> {
    return this.httpClient.get<Desk>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Create a new desk
   */
  create(desk: Omit<Desk, 'id'>): Observable<Desk> {
    return this.httpClient.post<Desk>(this.BASE_URL, desk);
  }

  /**
   * Update an existing desk
   */
  update(id: number, desk: Partial<Desk>): Observable<Desk> {
    return this.httpClient.put<Desk>(`${this.BASE_URL}/${id}`, desk);
  }

  /**
   * Delete a desk
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.BASE_URL}/${id}`);
  }
}
