import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Room } from '../models';
import { API_CONFIG } from './api.config';

/**
 * Room Service
 * Handles all HTTP operations for Room entities
 */
@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private apiUrl = API_CONFIG.baseUrl + API_CONFIG.endpoints.rooms;

  constructor(private httpClient: HttpClient) {}

  /**
   * Fetch all rooms
   */
  list(): Observable<Room[]> {
    return this.httpClient.get<Room[]>(this.apiUrl);
  }

  /**
   * Fetch all rooms in a specific floor
   */
  listByFloor(floorId: number): Observable<Room[]> {
    const url = `${this.apiUrl}/floor/${floorId}`;
    console.log('📡 RoomService.listByFloor() calling:', url);
    return this.httpClient.get<Room[]>(url);
  }

  /**
   * Fetch a single room by ID
   */
  get(id: number): Observable<Room> {
    return this.httpClient.get<Room>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new room
   */
  create(room: Omit<Room, 'id'>): Observable<Room> {
    return this.httpClient.post<Room>(this.apiUrl, room);
  }

  /**
   * Update an existing room
   */
  update(id: number, room: Partial<Room>): Observable<Room> {
    return this.httpClient.put<Room>(`${this.apiUrl}/${id}`, room);
  }

  /**
   * Delete a room
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/${id}`);
  }
}
