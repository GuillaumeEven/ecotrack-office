import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse, UserMeRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly BASE_URL = 'http://localhost:8080/api/v1/users';

  constructor(private http: HttpClient) {}

  getMe(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.BASE_URL}/me`);
  }

  updateMe(data: UserMeRequest): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.BASE_URL}/me`, data);
  }
}
