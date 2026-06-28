import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { environment } from '@environments/environment';
import { firstValueFrom } from 'rxjs';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerSpy: any;

  beforeEach(async () => {
    const routerSpyObj = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: Router, useValue: routerSpyObj }
      ]
    }).compileComponents();

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    routerSpy = TestBed.inject(Router) as any;

    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  /**
   * Test: AuthService.login()
   *
   * Dado: credenciales válidas
   * Cuando: se llama al método login()
   * Entonces: debe hacer POST y guardar el token + datos en localStorage
   */
  it('should login successfully and save session data', async () => {
    const loginRequest = { email: 'admin@ecotrack.local', password: 'password' };
    const mockResponse = {
      token: 'jwt_token_123',
      userId: 1,
      role: 'ADMIN',
      email: 'admin@ecotrack.local'
    };

    const promise = firstValueFrom(service.login(loginRequest));

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);

    await promise;

    expect(localStorage.getItem('auth_token')).toBe('jwt_token_123');
    expect(localStorage.getItem('user_id')).toBe('1');
    expect(localStorage.getItem('user_role')).toBe('ADMIN');
    expect(localStorage.getItem('user_email')).toBe('admin@ecotrack.local');
  });

  /**
   * Test: AuthService.logout()
   *
   * Dado: usuario autenticado con datos en localStorage
   * Cuando: se llama al método logout()
   * Entonces: debe borrar localStorage y navegar a /login
   */
  it('should clear localStorage and redirect to login on logout', () => {
    // Setup: simular usuario autenticado
    localStorage.setItem('auth_token', 'jwt_token_123');
    localStorage.setItem('user_id', '1');
    localStorage.setItem('user_role', 'ADMIN');

    service.logout();

    expect(localStorage.getItem('auth_token')).toBeNull();
    expect(localStorage.getItem('user_id')).toBeNull();
    expect(localStorage.getItem('user_role')).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['']);
  });

  /**
   * Test: AuthService.isAuthenticated()
   *
   * Dado: usuario autenticado vs no autenticado
   * Cuando: se llama isAuthenticated()
   * Entonces: retorna true si hay token, false si no
   */
  it('should return correct authentication status', () => {
    // Sin token
    expect(service.isAuthenticated()).toBe(false);

    // Con token
    localStorage.setItem('auth_token', 'jwt_token_123');
    expect(service.isAuthenticated()).toBe(true);
  });

  /**
   * Test: AuthService getters
   *
   * Dado: datos guardados en localStorage
   * Cuando: se llaman los getters (getToken, getUserId, getRole, getEmail)
   * Entonces: retornan los valores correctos
   */
  it('should return session data from localStorage', () => {
    localStorage.setItem('auth_token', 'jwt_token_123');
    localStorage.setItem('user_id', '42');
    localStorage.setItem('user_role', 'USER');
    localStorage.setItem('user_email', 'user@ecotrack.local');

    expect(service.getToken()).toBe('jwt_token_123');
    expect(service.getUserId()).toBe('42');
    expect(service.getRole()).toBe('USER');
    expect(service.getEmail()).toBe('user@ecotrack.local');
  });
});
