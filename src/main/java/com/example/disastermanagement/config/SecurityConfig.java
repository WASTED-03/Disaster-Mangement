package com.example.disastermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/debug/public").permitAll()
                        .requestMatchers("/alerts/latest").permitAll()
                        .requestMatchers("/alerts/near").permitAll()
                        .requestMatchers("/alerts/recent").permitAll() // User accessible - recent alerts
                        .requestMatchers("/resources/**").permitAll()
                        .requestMatchers("/weather/**").permitAll()

                        // USER SOS ENDPOINTS
                        .requestMatchers("/sos/create").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/sos/my").hasAnyAuthority("USER", "ADMIN")
                        // USER REPORT ENDPOINTS
                        .requestMatchers("/reports/create").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/reports/my/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/reports/{id}/edit").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/reports/{id}").hasAnyAuthority("USER", "ADMIN")

                        // ADMIN PROMOTION (authenticated users can request promotion)
                        .requestMatchers("/admin/promote").authenticated()

                        // ADMIN ONLY ENDPOINTS
                        .requestMatchers("/admin/users/**").hasAuthority("ADMIN")
                        .requestMatchers("/alerts/all").hasAuthority("ADMIN") // Admin only - all alerts
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")


                        // OTHER
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
