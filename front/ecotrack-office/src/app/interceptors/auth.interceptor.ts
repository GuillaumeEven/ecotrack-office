import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  // Si hay token lo añadimos al header Authorization
  const token = authService.getToken();
  const cloned = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(cloned).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Token expirado o inválido → logout y redirigir al login
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    }),
  );
};
