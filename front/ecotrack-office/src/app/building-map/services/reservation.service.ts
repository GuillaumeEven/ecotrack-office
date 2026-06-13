import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Reservation } from '../models';
import { API_CONFIG } from './api.config';

/**
 * Reservation Service
 * Handles all HTTP operations for Reservation entities
 */
@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = API_CONFIG.baseUrl + '/reservations';

  constructor(private httpClient: HttpClient) {}

  /**
   * Fetch all reservations for a floor on a specific date
   * @param floorId - The floor ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  getByFloorAndDate(floorId: number, date: string): Observable<Reservation[]> {
    const url = `${this.apiUrl}/floor/${floorId}/date/${date}`;
    console.log('📡 ReservationService.getByFloorAndDate() calling:', url);
    return this.httpClient.get<Reservation[]>(url);
  }

  /**
   * Fetch all reservations for a room on a specific date
   * @param roomId - The room ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  getByRoomAndDate(roomId: number, date: string): Observable<Reservation[]> {
    const url = `${this.apiUrl}/room/${roomId}/date/${date}`;
    console.log('📡 ReservationService.getByRoomAndDate() calling:', url);
    return this.httpClient.get<Reservation[]>(url);
  }

  /**
   * Fetch all reservations for a desk on a specific date
   * @param deskId - The desk ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  getByDeskAndDate(deskId: number, date: string): Observable<Reservation[]> {
    const url = `${this.apiUrl}/desk/${deskId}/date/${date}`;
    console.log('📡 ReservationService.getByDeskAndDate() calling:', url);
    return this.httpClient.get<Reservation[]>(url);
  }

  /**
   * Create a new reservation
   * @param userId - The user ID
   * @param resourceId - The resource (desk/room) ID
   * @param date - The date in ISO format (YYYY-MM-DD)
   */
  create(userId: number, resourceId: number, date: string): Observable<any> {
    const url = this.apiUrl;
    // Match the exact payload structure from Postman that works (status CONFIRMED)
    const payload = {
      date,
      status: 'CONFIRMED',
      userId,
      resourceId
    };
    console.log('� ReservationService.create() - URL:', url);
    console.log('🚀 ReservationService.create() - Payload:', JSON.stringify(payload, null, 2));
    console.log('🚀 Types - userId:', typeof userId, 'resourceId:', typeof resourceId, 'date:', typeof date);
    return this.httpClient.post(url, payload);
  }
}
