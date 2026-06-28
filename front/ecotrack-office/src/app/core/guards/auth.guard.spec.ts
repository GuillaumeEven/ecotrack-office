import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '@services/auth.service';

describe('authGuard', () => {
  let authServiceSpy: any;
  let routerSpy: any;

  beforeEach(() => {
    const authServiceSpyObj = { isAuthenticated: vi.fn() };
    const routerSpyObj = { navigate: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpyObj },
        { provide: Router, useValue: routerSpyObj }
      ]
    });

    authServiceSpy = TestBed.inject(AuthService) as any;
    routerSpy = TestBed.inject(Router) as any;
  });

  /**
   * Test: authGuard permite acceso a usuarios autenticados
   *
   * Dado: usuario autenticado (isAuthenticated = true)
   * Cuando: intenta acceder a una ruta protegida por authGuard
   * Entonces: retorna true y permite el acceso
   */
  it('should return true and allow access when user is authenticated', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(true);

    const mockRoute = {} as ActivatedRouteSnapshot;
    const mockState = {} as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, mockState));

    expect(result).toBe(true);
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  /**
   * Test: authGuard bloquea acceso a usuarios no autenticados
   *
   * Dado: usuario NO autenticado (isAuthenticated = false)
   * Cuando: intenta acceder a una ruta protegida por authGuard
   * Entonces: retorna false y redirige a /login
   */
  it('should return false and redirect to login when user is not authenticated', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(false);

    const mockRoute = {} as ActivatedRouteSnapshot;
    const mockState = {} as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, mockState));

    expect(result).toBe(false);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
