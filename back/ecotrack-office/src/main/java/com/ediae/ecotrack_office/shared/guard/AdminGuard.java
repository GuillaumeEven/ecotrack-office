package com.ediae.ecotrack_office.shared.guard;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.users.enums.Role;

@Component
public class AdminGuard {

    private final RoleGuard roleGuard;

    public AdminGuard(RoleGuard roleGuard) {
        this.roleGuard = roleGuard;
    }

    public void requireAdmin(Authentication auth) {
        roleGuard.requireRole(auth, Role.ADMIN);
    }
}