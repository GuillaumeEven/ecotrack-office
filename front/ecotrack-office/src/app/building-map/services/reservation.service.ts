import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Reservation } from '../models';
import { environment } from '../../../environments/environment';

/**
 * Reservation Service
 * Handles all HTTP operations for Reservation entities
 */
@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly BASE_URL = `${environment.apiUrl}/reservations`;

  constructor(private httpClient: HttpClient) {}

  /**
   * Fetch all reservations for a floor on a specific date
   * @param floorId - The floor ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  getByFloorAndDate(floorId: number, date: string): Observable<Reservation[]> {
    const url = `${this.BASE_URL}/floor/${floorId}/date/${date}`;
    return this.httpClient.get<Reservation[]>(url);
  }

  /**
   * Fetch all reservations for a room on a specific date
   * @param roomId - The room ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  getByRoomAndDate(roomId: number, date: string): Observable<Reservation[]> {
    const url = `${this.BASE_URL}/room/${roomId}/date/${date}`;
    return this.httpClient.get<Reservation[]>(url);
  }

  /**
   * Fetch all reservations for a desk on a specific date
   * @param deskId - The desk ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  getByDeskAndDate(deskId: number, date: string): Observable<Reservation[]> {
    const url = `${this.BASE_URL}/desk/${deskId}/date/${date}`;
    return this.httpClient.get<Reservation[]>(url);
  }

  /**
   * Create a new reservation
   * @param userId - The user ID
   * @param resourceId - The resource (desk/room) ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  create(userId: number, resourceId: number, date: string): Observable<any> {
    const url = this.BASE_URL;
    // Match the exact payload structure from Postman that works (status CONFIRMED)
    const payload = {
      date,
      status: 'CONFIRMED',
      userId,
      resourceId
    };
    return this.httpClient.post(this.BASE_URL, payload);
  }

  /**
   * Delete a reservation by ID
   * @param reservationId - The reservation ID to delete
   */
  delete(reservationId: number): Observable<boolean> {
    const url = `${this.BASE_URL}/${reservationId}`;
    return this.httpClient.delete<boolean>(url);
  }
}
