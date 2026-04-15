package com.dsw.practica02.empleados.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String EMPLEADO_ROLE = "EMPLEADO";
    private static final String SWAGGER_ROLE = "SWAGGER";

    @Bean
    @Order(1)
    SecurityFilterChain swaggerSecurityFilterChain(
            HttpSecurity http,
            AuthenticationProvider swaggerAuthenticationProvider
    ) throws Exception {
        http
                .securityMatcher("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(swaggerAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain appSecurityFilterChain(
            HttpSecurity http,
            BootstrapEmpleadoAuthorizationManager bootstrapEmpleadoAuthorizationManager,
            ObjectMapper objectMapper,
            AuthenticationProvider empleadoAuthenticationProvider
        ) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(empleadoAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/empleados").access(bootstrapEmpleadoAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/api/v1/empleados/**").hasAnyRole(ADMIN_ROLE, EMPLEADO_ROLE)
                        .requestMatchers("/api/v1/auth/me").authenticated()
                        .requestMatchers("/api/v1/empleados/**").hasRole(ADMIN_ROLE)
                        .requestMatchers("/api/v1/departamentos/**").hasRole(ADMIN_ROLE)
                        .anyRequest().hasRole(ADMIN_ROLE)
                )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    ApiError payload = ApiError.of(HttpStatus.UNAUTHORIZED, "Autenticación requerida", request);
                    writeSecurityError(response, objectMapper, payload);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    ApiError payload = ApiError.of(HttpStatus.FORBIDDEN, "Acceso denegado", request);
                    writeSecurityError(response, objectMapper, payload);
                })
            )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService empleadoUserDetailsService(EmpleadoRepository empleadoRepository) {
        return username -> {
            String email = username == null ? null : username.trim().toLowerCase(Locale.ROOT);
            return empleadoRepository.findByEmailIgnoreCase(email)
                    .map(empleado -> User.builder()
                    .username(empleado.getEmail())
                            .password(empleado.getPassword())
                    .roles(empleado.getRole().name())
                            .build())
                    .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                            "Empleado no encontrado"));
        };
    }

    @Bean
    UserDetailsService swaggerUserDetailsService(Environment environment, PasswordEncoder passwordEncoder) {
        String swaggerUsername = propertyOrDefault(environment, "SWAGGER_USERNAME", "swagger");
        String swaggerPassword = propertyOrDefault(environment, "SWAGGER_PASSWORD", "swagger123");

        UserDetails swaggerUser = User.builder()
                .username(swaggerUsername)
                .password(passwordEncoder.encode(swaggerPassword))
                .roles(SWAGGER_ROLE)
                .build();

        return new InMemoryUserDetailsManager(swaggerUser);
    }

    @Bean
    AuthenticationProvider empleadoAuthenticationProvider(
            @Qualifier("empleadoUserDetailsService") UserDetailsService empleadoUserDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(empleadoUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationProvider swaggerAuthenticationProvider(
            @Qualifier("swaggerUserDetailsService") UserDetailsService swaggerUserDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(swaggerUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private String propertyOrDefault(Environment environment, String key, String fallback) {
        String value = environment.getProperty(key);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static void writeSecurityError(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            ApiError payload
    ) throws IOException {
        response.setStatus(payload.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), payload);
    }
}
