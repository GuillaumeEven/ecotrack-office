package com.ediae.ecotrack_office.users.dto;


public record UserMeRequestDto(
    String firstName,
    String lastName,
    String preferencesJson
) {}