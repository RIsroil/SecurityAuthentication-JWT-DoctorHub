package com.example.demo.config;

import com.example.demo.jwt.JwtAuthenticationFilter;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;


    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (accessible to everyone, no authentication)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/patient/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/doctor/register").permitAll() // Adjust if admin-only
                        .requestMatchers(HttpMethod.GET, "/specialization/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/specialization/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/certificate/**").permitAll() // For doctor certificates
                        .requestMatchers(HttpMethod.GET, "/branch/**").permitAll() // For branch details
                        .requestMatchers(HttpMethod.GET, "/addresses").permitAll() // Get all addresses public
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/webjars/swagger-ui/**").permitAll()

                        // Doctor-specific endpoints
                        .requestMatchers(HttpMethod.POST, "/branch").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/branch").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/branch/**").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/branch/**").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/certificate/upload").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/certificate").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/certificate").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/certificate/**").hasAuthority("ROLE_DOCTOR")

                        // Patient-specific endpoints
                        .requestMatchers(HttpMethod.POST, "/chat").hasAuthority("ROLE_PATIENT")
                        .requestMatchers(HttpMethod.POST, "/appointment/**").hasAuthority("ROLE_PATIENT")

                        // Authenticated endpoints (any authenticated user with specific roles)
                        .requestMatchers(HttpMethod.GET, "/auth/profile").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/auth/update").authenticated()
                        .requestMatchers(HttpMethod.POST, "/auth/change-password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/message/**").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/message/**").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/message/**").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/chat").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/chat/**").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/chat/**").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/appointment").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/appointment/**").hasAnyAuthority("ROLE_DOCTOR","ROLE_PATIENT")
                        .requestMatchers(HttpMethod.GET, "/disease").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/disease/**").hasAnyAuthority("ROLE_PATIENT", "ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/disease").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/disease/**").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/disease/**").hasAuthority("ROLE_DOCTOR")

                        // Admin-only endpoints for addresses
                        .requestMatchers(HttpMethod.POST, "/addresses").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/addresses/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/addresses/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/addresses/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/addresses/id").hasAuthority("ROLE_ADMIN")

                        // Admin-only endpoints for certificate status
                        .requestMatchers(HttpMethod.PATCH, "/certificate/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/certificate/status").hasAuthority("ROLE_ADMIN")

                        // Admin has access to ALL endpoints
                        .requestMatchers("/**").hasAuthority("ROLE_ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:4200","https://doctorhub.vercel.app"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
