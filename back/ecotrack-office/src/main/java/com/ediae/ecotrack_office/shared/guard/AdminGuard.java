package com.ediae.ecotrack_office.shared.guard;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.shared.exception.ForbiddenException;

@Component
public class AdminGuard {

    public void requireAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("No estás autenticado.");
        }

        // El JwtFilter guarda el rol como "ROLE_ADMIN" en las authorities
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new ForbiddenException("Necesitas ser ADMIN para realizar esta acción.");
        }
    }
}