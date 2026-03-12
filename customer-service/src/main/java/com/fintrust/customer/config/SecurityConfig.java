package com.fintrust.customer.config;

import com.fintrust.common.security.JwtAuthenticationFilter;
import com.fintrust.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/accounts/register", "/api/accounts/documents/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Internal service-to-service endpoints (called via Feign, no JWT)
                        .requestMatchers("/api/accounts/*/balance", "/api/accounts/*/validate", "/api/accounts/internal/**").permitAll()
                        // Admin endpoints
                        .requestMatchers("/api/accounts/admin/**").hasRole("ADMIN")
                        // Customer-facing endpoints (behind API gateway with JWT)
                        .requestMatchers("/api/accounts/summary", "/api/accounts/cards/**", "/api/accounts/fd/**", "/api/beneficiaries/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
