import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { DeskModel } from '@models/index.model';
import { environment } from '@environments/environment';
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
  list(): Observable<DeskModel[]> {
    return this.httpClient.get<DeskModel[]>(this.BASE_URL);
  }

  /**
   * Fetch all desks in a specific room
   */
  listByRoom(roomId: number): Observable<DeskModel[]> {
    return this.httpClient.get<DeskModel[]>(`${this.BASE_URL}/room/${roomId}`);
  }

  /**
   * Fetch a single desk by ID
   */
  get(id: number): Observable<DeskModel> {
    return this.httpClient.get<DeskModel>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Fetch all incidents associated with a specific desk
   */
  getIncidentsByDeskId(deskId: number): Observable<any[]> {
    console.log(`Fetching incidents for desk ID: ${deskId}`);
    return this.httpClient.get<any[]>(`${this.BASE_URL}/${deskId}/incidents`);
  }

  /**
   * Create a new desk
   */
  create(desk: Omit<DeskModel, 'id'>): Observable<DeskModel> {
    return this.httpClient.post<DeskModel>(this.BASE_URL, desk);
  }

  /**
   * Update an existing desk
   */
  update(id: number, desk: Partial<DeskModel>): Observable<DeskModel> {
    return this.httpClient.put<DeskModel>(`${this.BASE_URL}/${id}`, desk);
  }

  /**
   * Delete a desk
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.BASE_URL}/${id}`);
  }
}
