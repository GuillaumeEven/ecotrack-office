package com.ediae.ecotrack_office.auth.service;

import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.auth.dto.LoginRequestDto;
import com.ediae.ecotrack_office.auth.dto.LoginResponseDto;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
    

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // ─────────────────────────────────────────────
    // Login — verifica credenciales y devuelve JWT
    // ─────────────────────────────────────────────
    public LoginResponseDto login(LoginRequestDto dto) {

        logger.info("Intento de service.login para el usuario: {}", dto);

        // 1. Buscamos el usuario por email
        UserEntity user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // 2. Verificamos que el usuario está activo
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("El usuario está desactivado");
        }

        // 3. Verificamos la contraseña de forma segura
        // 🆕 Cambiado de .equals() al método matches() de BCrypt
        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            // Nota de TFM: Por seguridad, si falla la contraseña, es mejor lanzar "Credenciales incorrectas"
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        // 4. Generamos el token con el id y el rol del usuario
        String token = jwtService.generateToken(user.getId(), user.getRole().name());

        return new LoginResponseDto(token, user.getId(), user.getRole().name(), user.getEmail());
    }
}
