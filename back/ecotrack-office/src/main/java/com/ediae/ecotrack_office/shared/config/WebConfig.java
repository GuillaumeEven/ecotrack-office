package com.ediae.ecotrack_office.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ediae.ecotrack_office.shared.interceptor.AuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    // Le decimos a Spring: "usa este interceptor en todas las rutas"
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // ← todas las rutas
                
                //MENOS LAS RUTAS PÚBLICAS COMO EL LOGIN Y EL REGISTRO
                .excludePathPatterns(
                    "/api/v1/users/public/create-user",
                    "/api/v1/organization/public/create",
                    "/api/v1/auth/loing"
                );
    }

    
}