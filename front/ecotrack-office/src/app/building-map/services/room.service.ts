import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Room } from '../models';
import { environment } from '../../../environments/environment';
/**
 * Room Service
 * Handles all HTTP operations for Room entities
 */
@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private readonly BASE_URL = `${environment.apiUrl}/rooms`;

  constructor(private httpClient: HttpClient) {}

  /**
   * Fetch all rooms
   */
  list(): Observable<Room[]> {
    return this.httpClient.get<Room[]>(this.BASE_URL);
  }

  /**
   * Fetch all rooms in a specific floor
   */
  listByFloor(floorId: number): Observable<Room[]> {
    const url = `${this.BASE_URL}/floor/${floorId}`;
    return this.httpClient.get<Room[]>(url);
  }

  /**
   * Fetch a single room by ID
   */
  get(id: number): Observable<Room> {
    return this.httpClient.get<Room>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Create a new room
   */
  create(room: Omit<Room, 'id'>): Observable<Room> {
    return this.httpClient.post<Room>(this.BASE_URL, room);
  }

  /**
   * Update an existing room
   */
  update(id: number, room: Partial<Room>): Observable<Room> {
    return this.httpClient.put<Room>(`${this.BASE_URL}/${id}`, room);
  }

  /**
   * Delete a room
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.BASE_URL}/${id}`);
  }
}
