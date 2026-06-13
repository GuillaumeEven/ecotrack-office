package com.ediae.ecotrack_office.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @Email(message = "El email no es válido")
    @NotBlank(message = "El email no puede estar vacío")
    String email,

    @NotBlank(message = "La contraseña no puede estar vacía")
    String password
) {}