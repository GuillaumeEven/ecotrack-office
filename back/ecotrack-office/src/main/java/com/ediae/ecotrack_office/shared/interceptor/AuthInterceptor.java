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

        
        String userIdHeader = request.getHeader("X-User-Id");
        String userRoleHeader = request.getHeader("X-User-Role");

        // Si vienen los dos headers, los guardamos en el contexto
        if (userIdHeader != null && userRoleHeader != null) {
            try {
                Long id = Long.parseLong(userIdHeader);
                Role role = Role.valueOf(userRoleHeader);
                RequestContext.set(id, role);
            } catch (Exception e) {
                // Si los headers tienen un formato incorrecto, rechazamos la petición
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
        }

        return true;
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