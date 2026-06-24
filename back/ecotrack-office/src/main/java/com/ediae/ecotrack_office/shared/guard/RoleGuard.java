package com.ediae.ecotrack_office.shared.guard;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.shared.exception.ForbiddenException;
import com.ediae.ecotrack_office.users.enums.Role;

@Component
public class RoleGuard {

    // ─────────────────────────────────────────────
    // Comprueba que el usuario tiene UN rol específico
    // Ejemplo: roleGuard.requireRole(auth, Role.TECHNICIAN)
    // ─────────────────────────────────────────────
    public void requireRole(Authentication auth, Role role) {
        checkAuthenticated(auth);
        if (!hasRole(auth, role)) {
            throw new ForbiddenException("No tienes permisos para realizar esta acción.");
        }
    }

    // ─────────────────────────────────────────────
    // Comprueba que el usuario tiene AL MENOS UNO de los roles indicados
    // Ejemplo: roleGuard.requireAnyRole(auth, Role.ADMIN, Role.TECHNICIAN)
    // ─────────────────────────────────────────────
    public void requireAnyRole(Authentication auth, Role... roles) {
        checkAuthenticated(auth);
        for (Role role : roles) {
            if (hasRole(auth, role)) return;
        }
        throw new ForbiddenException("No tienes permisos para realizar esta acción.");
    }

    // ─────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────
    private boolean hasRole(Authentication auth, Role role) {
    return auth.getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_" + role.name()));
}

    // ─────────────────────────────────────────────
    // Public helper para verificar roles sin lanzar excepción
    // ─────────────────────────────────────────────
    public boolean hasAnyRole(Authentication auth, Role... roles) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        for (Role role : roles) {
            if (hasRole(auth, role)) return true;
        }
        return false;
    }

    private void checkAuthenticated(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("No estás autenticado.");
        }
    }
}