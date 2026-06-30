package com.ediae.ecotrack_office.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.auth.dto.LoginRequestDto;
import com.ediae.ecotrack_office.auth.dto.LoginResponseDto;
import com.ediae.ecotrack_office.shared.exception.ApplicationException;
import com.ediae.ecotrack_office.shared.exception.ErrorCode;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.repository.UserRepository;


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

    public LoginResponseDto login(LoginRequestDto dto) {

        UserEntity user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (!user.getIsActive()) {

            throw new ApplicationException(ErrorCode.FORBIDDEN, "El usuario está desactivado");
        }
        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {

            throw new ApplicationException(ErrorCode.INVALID_CREDENTIALS, "Credenciales incorrectas");
        }
        String token = jwtService.generateToken(user.getId(), user.getRole().name());

        return new LoginResponseDto(token, user.getId(), user.getRole().name(), user.getEmail());
    }
}
