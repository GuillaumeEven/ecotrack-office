package com.ediae.ecotrack_office.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Test Configuration para proporcionar beans necesarios durante tests de integración
 */
@TestConfiguration
public class TestConfig {

    /**
     * Proporciona ObjectMapper para serialización/deserialización JSON en tests
     * Incluye módulo JSR310 para manejar LocalDate, LocalDateTime, etc.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
