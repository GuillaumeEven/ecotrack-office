import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

// MVP: valores hardcodeados hasta que se implemente autenticación real.
// Cuando se añada JWT, sustituir estas dos constantes por los valores
// extraídos del token (id y role del claim).
const CURRENT_USER_ID = 1;
const CURRENT_USER_ROLE = 'USER';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  const cloned = req.clone({
    setHeaders: {
      'X-User-Id': String(CURRENT_USER_ID),
      'X-User-Role': CURRENT_USER_ROLE,
    },
  });

  return next(cloned).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Sesión expirada o no autorizado → redirigir al login
        // TODO: cuando haya login real, redirigir a '/login'
        console.warn('401 Unauthorized - redirigir al login');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    }),
  );
};
