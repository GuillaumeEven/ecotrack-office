package com.ediae.ecotrack_office.shared.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ediae.ecotrack_office.shared.context.RequestContext;
import com.ediae.ecotrack_office.users.enums.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // Se ejecuta ANTES de que la petición llegue al controller
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // TODO: Quitar esta excepción cuando el frontend implemente credenciales correctas
        // Las rutas GET de floors son públicas temporalmente para desarrollo
        // List<String> publicPaths = List.of(
        //         "/api/v1/floors",
        //         "/api/v1/reservations"
        // );
        // if (publicPaths.stream().anyMatch(path::contains) && "GET".equals(method)) {
        //     return true;
        // }

        String userIdHeader = request.getHeader("X-User-Id");
        String userRoleHeader = request.getHeader("X-User-Role");

        // Si vienen los dos headers, los guardamos en el contexto
        if (userIdHeader != null && userRoleHeader != null) {
            try {
                Long id = Long.parseLong(userIdHeader);
                Role role = Role.valueOf(userRoleHeader); // "ADMIN" → Role.ADMIN
                RequestContext.set(id, role);
            } catch (Exception e) {
                // Si los headers tienen un formato incorrecto, rechazamos la petición
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false; // false = no sigas, no llegues al controller
            }
        }

        return true; // true = todo bien, sigue hacia el controller
    }

    // Se ejecuta SIEMPRE al final de la petición, aunque haya fallado
    // Aquí limpiamos el contexto para no dejar datos del usuario anterior
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        RequestContext.clear();
    }
}