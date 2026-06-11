import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse, UserMeRequest, ChangePasswordRequest } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly BASE_URL = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getMe(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.BASE_URL}/me`);
  }

  updateMe(data: UserMeRequest): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.BASE_URL}/me`, data);
  }

  changePassword(data: ChangePasswordRequest): Observable<void> {
    return this.http.patch<void>(`${this.BASE_URL}/me/password`, data);
  }
}
