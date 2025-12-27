package org.saad.billingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Billing Service.
 * Validates JWT tokens using Keycloak public key.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()
                                                // Allow internal access from chatbot/MCP
                                                .requestMatchers("/api/**").permitAll()
                                                // All other requests require authentication
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(Customizer.withDefaults()))
                                // Allow H2 console frames
                                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

                return http.build();
        }
}
