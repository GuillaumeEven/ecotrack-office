import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Formato de la API: {"status":404,"error":"Not Found","message":"Usuario no encontrado"}
      const errorMessage = error.error?.message || error.error?.error || error.statusText || 'Error desconocido';
      notificationService.error(errorMessage);

      console.error('Error capturado por interceptor:', error);
      return throwError(() => error);
    }),
  );
};
