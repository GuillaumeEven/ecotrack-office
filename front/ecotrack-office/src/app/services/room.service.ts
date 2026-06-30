import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { RoomModel } from '@models/index.model';
import { environment } from '@environments/environment';
/**
 * Room Service
 * Handles all HTTP operations for Room entities
 */
@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private readonly BASE_URL = `${environment.apiUrl}/rooms`;

  constructor(private httpClient: HttpClient) { }

  /**
   * Fetch all rooms
   */
  list(): Observable<RoomModel[]> {
    return this.httpClient.get<RoomModel[]>(this.BASE_URL);
  }

  /**
   * Fetch all rooms in a specific floor
   */
  listByFloor(floorId: number): Observable<RoomModel[]> {
    const url = `${this.BASE_URL}/floor/${floorId}`;
    return this.httpClient.get<RoomModel[]>(url);
  }

  /**
   * Fetch a single room by ID
   */
  get(id: number): Observable<RoomModel> {
    return this.httpClient.get<RoomModel>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Fetch all incidents associated with a specific room
   */
  getIncidentsByRoomId(roomId: number): Observable<any[]> {
    return this.httpClient.get<any[]>(`${this.BASE_URL}/${roomId}/incidents`);
  }

  /**
   * Create a new room
   */
  create(room: Omit<RoomModel, 'id'>): Observable<RoomModel> {
    return this.httpClient.post<RoomModel>(this.BASE_URL, room);
  }

  /**
   * Update an existing room
   */
  update(id: number, room: Partial<RoomModel>): Observable<RoomModel> {
    return this.httpClient.put<RoomModel>(`${this.BASE_URL}/${id}`, room);
  }

  /**
   * Delete a room
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.BASE_URL}/${id}`);
  }
}
