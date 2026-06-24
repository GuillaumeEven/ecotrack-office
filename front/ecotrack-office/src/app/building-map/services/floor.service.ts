import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Floor, FloorWithStatus } from '../models';
import { environment } from '../../../environments/environment';
import { UserService } from '../../services/user.service';
/**
 * Floor Service
 * Handles all HTTP operations for Floor entities
 */
@Injectable({
  providedIn: 'root'
})
export class FloorService {
  // private apiUrl = API_CONFIG.baseUrl + API_CONFIG.endpoints.floors;
  private readonly BASE_URL = `${environment.apiUrl}/floors`;

  private organizationId: number | undefined;


  constructor(private httpClient: HttpClient, private userService: UserService) {
    // get el organizationId por el usuario logueado
    this.userService.getMe().subscribe((user) => {
      // Update the organizationId based on the logged-in user
      this.organizationId = user.organizationId;
    });
  }

  /**
   * Fetch all floors for an organization
   */
  list(organizationId: number = this.organizationId!): Observable<Floor[]> {
    const url = `${this.BASE_URL}/organization/${organizationId}`;
    return this.httpClient.get<Floor[]>(url);
  }

  /**
   * Fetch a single floor by ID
   */
  get(id: number): Observable<Floor> {
    return this.httpClient.get<Floor>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Create a new floor
   */
  create(floor: Omit<Floor, 'id'>): Observable<Floor> {
    return this.httpClient.post<Floor>(this.BASE_URL, floor);
  }

  /**
   * Update an existing floor
   */
  update(id: number, floor: Partial<Floor>): Observable<Floor> {
    return this.httpClient.put<Floor>(`${this.BASE_URL}/${id}`, floor);
  }

  /**
   * Delete a floor
   */
  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.BASE_URL}/${id}`);
  }

  /**
   * Fetch all floors with their rooms, desks and status for a specific date
   * Global progressive unlock logic applied across all floors
   *
   * @param organizationId Organization ID
   * @param date Date in format YYYY-MM-DD
   * @returns Observable of FloorWithStatus[] (all floors with global progressive logic applied)
   */
  getFloorsWithStatus(organizationId: number = this.organizationId!, date: string): Observable<FloorWithStatus[]> {
    const url = `${this.BASE_URL}/status/${organizationId}`;
    const params = new HttpParams().set('date', date);
    return this.httpClient.get<FloorWithStatus[]>(url, { params });
  }
}
