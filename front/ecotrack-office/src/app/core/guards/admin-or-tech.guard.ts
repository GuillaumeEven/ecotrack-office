import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

export const adminOrTechGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  const role = authService.getRole();
  const hasAccess = authService.isAuthenticated() && (role === 'ADMIN' || role === 'TECHNICIAN');

  if (hasAccess) {
    return true;
  }

  notificationService.error('Acceso denegado. Solo administradores o técnicos pueden acceder a esta sección.');
  router.navigate(['/home']);

  return false;
};