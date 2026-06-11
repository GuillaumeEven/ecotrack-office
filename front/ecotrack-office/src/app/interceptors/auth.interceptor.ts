import { HttpInterceptorFn } from '@angular/common/http';

// MVP: valores hardcodeados hasta que se implemente autenticación real.
// Cuando se añada JWT, sustituir estas dos constantes por los valores
// extraídos del token (id y role del claim).
const CURRENT_USER_ID = 1;
const CURRENT_USER_ROLE = 'USER';

// Interceptor que añade los headers X-User-Id y X-User-Role a cada petición HTTP.
// Basicamente clona la petición original y le añade los headers con el id y role del usuario actual 
// porque no se puede clonar la petición original directamente. 
// Luego, pasa la petición clonada al siguiente interceptor o al backend.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const cloned = req.clone({
    setHeaders: {
      'X-User-Id': String(CURRENT_USER_ID),
      'X-User-Role': CURRENT_USER_ROLE,
    },
  });
  return next(cloned);
};
