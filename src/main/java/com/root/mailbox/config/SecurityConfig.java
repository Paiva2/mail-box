package com.root.mailbox.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true)
@AllArgsConstructor
public class SecurityConfig {
    private final SecurityAuthFilter securityAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(c -> {
                CorsConfiguration cfg = new CorsConfiguration();
                cfg.addAllowedHeader("*");

                cfg.setAllowedOrigins(List.of("http://localhost:3000"));
                cfg.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
                cfg.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", cfg);

                return cfg;
            }))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> {
                    request.requestMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll();
                    request.requestMatchers(HttpMethod.PATCH, "/api/v1/user/forgot-password").permitAll();
                    request.requestMatchers("/api/v1/auth/**", "/error").permitAll();
                    request.requestMatchers("/ws/info", "/ws/**").permitAll();

                    request.anyRequest().authenticated();
                }
            ).addFilterBefore(securityAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
