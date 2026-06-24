import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // 1. Comprobamos si está autenticado y si su rol es estrictamente 'ADMIN'
  if (authService.isAuthenticated() && authService.getRole() === 'ADMIN') {
    return true; // Acceso concedido
  }

  // 2. Si no es Admin, lo mandamos a su perfil o home privado para que no se quede bloqueado en blanco
  router.navigate(['/profile']);
  return false;
};
