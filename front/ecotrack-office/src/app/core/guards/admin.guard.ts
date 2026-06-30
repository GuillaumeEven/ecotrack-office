import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@services/auth.service';
import { NotificationService } from '@services/notification.service'; // Importamos NotificationService

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const notificationService = inject(NotificationService); // Inyectamos NotificationService

  // 1. Comprobamos si está autenticado y si su rol es estrictamente 'ADMIN'
  if (authService.isAuthenticated() && authService.getRole() === 'ADMIN') {
    return true; // Acceso concedido
  }

  // 2. Si no es Admin, mostramos una notificación y lo mandamos a su perfil o home privado
  notificationService.error('Acceso denegado. Solo los administradores pueden acceder a esta sección.');
  router.navigate(['/home']);

  return false;
};
