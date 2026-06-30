import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import {
  UserResponse,
  UserMeRequest,
  ChangePasswordRequest,
  CreateUserRequest
} from '@models/index.model';
import { environment } from '@environments/environment';

export interface PageResponse<T> {
  content: T[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
}

export interface UserStats {
  totalUsers: number;
  activeUsers: number;
  newThisMonth: number;
}

export interface UserFilters {
  search?: string;
  role?: string;
  isActive?: boolean;
  page?: number;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly BASE_URL = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  // ─── Endpoints de usuario ────────────────────────────────────────────────
  getMe(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.BASE_URL}/me`);
  }

  updateMe(data: UserMeRequest): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.BASE_URL}/me`, data);
  }

  changePassword(data: ChangePasswordRequest): Observable<void> {
    return this.http.patch<void>(`${this.BASE_URL}/me/password`, data);
  }

  registerAndAssociate(data: CreateUserRequest): Observable<UserResponse> {

    return this.http.post<UserResponse>(`${this.BASE_URL}/public/create-user`, data);
  }

  // ─── Endpoints de admin ───────────────────────────────────────────────────

  getUsers(
    organizationId: number,
    filters: UserFilters = {},
  ): Observable<PageResponse<UserResponse>> {
    if (!this.isValidOrganizationId(organizationId)) {
      return throwError(() => new Error('organizationId must be a valid number'));
    }

    let params = new HttpParams()
      .set('organizationId', String(organizationId))
      .set('page', String(filters.page ?? 0))
      .set('size', String(filters.size ?? 10));

    if (filters.search) params = params.set('search', filters.search);
    if (filters.role) params = params.set('role', filters.role);
    if (filters.isActive !== undefined) params = params.set('isActive', String(filters.isActive));

    return this.http.get<PageResponse<UserResponse>>(this.BASE_URL, { params });
  }

  getUserStats(organizationId: number): Observable<UserStats> {
    if (!this.isValidOrganizationId(organizationId)) {
      return throwError(() => new Error('organizationId must be a valid number'));
    }

    const params = new HttpParams().set('organizationId', String(organizationId));
    return this.http.get<UserStats>(`${this.BASE_URL}/stats`, { params });
  }

  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.BASE_URL}/${id}`);
  }

  updateUser(id: number, data: Partial<UserResponse>): Observable<UserResponse> {
    console.log(`Updating user with ID ${id}:`, data);
    return this.http.put<UserResponse>(`${this.BASE_URL}/${id}`, data);
  }

  deactivateUser(id: number): Observable<void> {
    return this.http.patch<void>(`${this.BASE_URL}/${id}/deactivate`, {});
  }

  reactivateUser(id: number): Observable<void> {
    return this.http.patch<void>(`${this.BASE_URL}/${id}/reactivate`, {});
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE_URL}/${id}`);
  }

  private isValidOrganizationId(value: number | null | undefined): value is number {
    return typeof value === 'number' && Number.isFinite(value);
  }
}
