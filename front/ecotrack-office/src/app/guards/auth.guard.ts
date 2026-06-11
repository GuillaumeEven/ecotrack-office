import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);

  // MVP: comprobación simple con el ID hardcodeado del interceptor.
  // TODO: cuando haya autenticación real, comprobar aquí si hay
  // token JWT válido en localStorage o en un AuthService.
  const isAuthenticated = true;

  if (!isAuthenticated) {
    // TODO: cambiar '/login' por la ruta real cuando el compañero mergee su PR
    router.navigate(['/login']);
    return false;
  }

  return true;
};
