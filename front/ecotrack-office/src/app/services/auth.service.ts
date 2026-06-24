import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly BASE_URL = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_ID_KEY = 'user_id';
  private readonly ROLE_KEY = 'user_role';
  private readonly EMAIL_KEY = 'user_email';

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  // ─── Login ───────────────────────────────────────────────────────────────

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.BASE_URL}/login`, data)
      .pipe(tap((response) => this.saveSession(response)));
  }

  // ─── Session management ───────────────────────────────────────────────────

  private saveSession(response: LoginResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem(this.USER_ID_KEY, String(response.userId));
    localStorage.setItem(this.ROLE_KEY, response.role);
    localStorage.setItem(this.EMAIL_KEY, response.email);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_ID_KEY);
    localStorage.removeItem(this.ROLE_KEY);
    localStorage.removeItem(this.EMAIL_KEY);
    this.router.navigate(['']);
  }

  // ─── Getters ──────────────────────────────────────────────────────────────

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUserId(): string | null {
    return localStorage.getItem(this.USER_ID_KEY);
  }

  getRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  getEmail(): string | null {
    return localStorage.getItem(this.EMAIL_KEY);
  }
}
