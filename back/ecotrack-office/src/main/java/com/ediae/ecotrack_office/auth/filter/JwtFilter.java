package com.ediae.ecotrack_office.auth.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ediae.ecotrack_office.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Leemos el header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay token o no empieza por "Bearer ", dejamos pasar
        //    Spring Security ya bloqueará las rutas protegidas
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraemos el token quitando el prefijo "Bearer "
        String token = authHeader.substring(7);

        // 4. Verificamos que el token es válido
        if (!jwtService.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 5. Extraemos el id y el rol del token
        Long userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        // 6. Le decimos a Spring quién es el usuario autenticado
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. Dejamos continuar la petición
        filterChain.doFilter(request, response);
    }
}