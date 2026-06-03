package com.ediae.ecotrack_office.shared.guard;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.shared.context.RequestContext;
import com.ediae.ecotrack_office.shared.exception.ForbiddenException;
import com.ediae.ecotrack_office.users.enums.Role;

@Component
public class AdminGuard {

    /**
     * Comprueba que el usuario que hace la petición es ADMIN.
     * Si no lo es, lanza ForbiddenException → Spring responde 403 Forbidden.
     */
    public void requireAdmin() {
        Role role = RequestContext.getUserRole();

        // Si no hay rol en el contexto, la petición no tiene headers → no autenticado
        if (role == null) {
            throw new ForbiddenException("No estás autenticado.");
        }

        // Si tiene rol pero no es ADMIN → prohibido
        if (role != Role.ADMIN) {
            throw new ForbiddenException("Necesitas ser ADMIN para realizar esta acción.");
        }
    }
}
