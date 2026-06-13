package com.ediae.ecotrack_office.shared.guard;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.shared.exception.ForbiddenException;
import com.ediae.ecotrack_office.users.enums.Role;

@Component
public class RoleGuard {

    // ═════════════════════════════════════════════════════════════════
    // MODO 1: Extracción de datos (boolean returns) - Para controladores
    // ═════════════════════════════════════════════════════════════════

    /**
     * Extract userId from Authentication context
     */
    public Long getUserIdFromAuth(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    /**
     * Check if user has a specific role (returns boolean, no exception)
     * Ejemplo: roleGuard.hasRole(auth, Role.ADMIN)
     */
    public boolean hasRole(Authentication auth, Role role) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Check if user has ANY of the specified roles
     * Ejemplo: roleGuard.hasAnyRole(auth, Role.ADMIN, Role.TECHNICIAN)
     */
    public boolean hasAnyRole(Authentication auth, Role... roles) {
        if (auth == null || !auth.isAuthenticated()) return false;
        for (Role role : roles) {
            if (hasRole(auth, role)) return true;
        }
        return false;
    }

    /**
     * Convenience method: Check if user is ADMIN
     */
    public boolean isAdmin(Authentication auth) {
        return hasRole(auth, Role.ADMIN);
    }

    /**
     * Convenience method: Check if user is TECHNICIAN
     */
    public boolean isTechnician(Authentication auth) {
        return hasRole(auth, Role.TECHNICIAN);
    }

    /**
     * Convenience method: Check if user is EMPLOYEE
     */
    public boolean isEmployee(Authentication auth) {
        return hasRole(auth, Role.EMPLOYEE);
    }

    // ═════════════════════════════════════════════════════════════════
    // MODO 2: Validación con excepciones - Para servicios/handlers
    // ═════════════════════════════════════════════════════════════════

    // ─────────────────────────────────────────────
    // Comprueba que el usuario tiene UN rol específico
    // Ejemplo: roleGuard.requireRole(auth, Role.TECHNICIAN)
    // ─────────────────────────────────────────────
    public void requireRole(Authentication auth, Role role) {
        checkAuthenticated(auth);
        if (!hasRole(auth, role)) {
            throw new ForbiddenException(
                "Necesitas el rol " + role.name() + " para realizar esta acción."
            );
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
    // Helper privado
    // ─────────────────────────────────────────────
    private void checkAuthenticated(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("No estás autenticado.");
        }
    }
}