package com.hrgenius.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. The web pages themselves are public (the login screen must load)
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // 2. Endpoints any logged-in person can use (about themselves)
                .requestMatchers("/api/auth/**",
                        "/api/employees/me",
                        "/api/attendance/check-in", "/api/attendance/check-out", "/api/attendance/my",
                        "/api/leaves/apply", "/api/leaves/my",
                        "/api/payroll/my",
                        "/api/reviews/my").hasAnyRole("EMPLOYEE", "HR", "ADMIN")
                // 3. Everything else under /api is only for HR and ADMIN
                .requestMatchers("/api/**").hasAnyRole("HR", "ADMIN")
                .anyRequest().permitAll())
            // Return plain 401 on bad login so the browser doesn't show its own popup
            .httpBasic(b -> b.authenticationEntryPoint((req, res, ex) -> res.sendError(401, "Unauthorized")));
        return http.build();
    }
}
