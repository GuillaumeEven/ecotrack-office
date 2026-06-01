package com.ediae.ecotrack_office.shared.context;

import com.ediae.ecotrack_office.users.enums.Role;

public class RequestContext {

    // ThreadLocal es como una variable global pero segura:
    // cada petición tiene la suya propia y no se mezclan entre sí
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<Role> userRole = new ThreadLocal<>();

    // El interceptor llama a esto al inicio de cada petición
    public static void set(Long id, Role role) {
        userId.set(id);
        userRole.set(role);
    }

    // El controller/service llama a esto para saber quién está llamando
    public static Long getUserId() {
        return userId.get();
    }

    public static Role getUserRole() {
        return userRole.get();
    }

    // El interceptor llama a esto al final de cada petición para limpiar
    // Si no limpiamos, los datos del usuario anterior podrían filtrarse
    public static void clear() {
        userId.remove();
        userRole.remove();
    }
}