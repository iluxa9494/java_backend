package ru.skillbox.socialnetwork.authentication.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.skillbox.socialnetwork.authentication.security.UserDetailsServiceImpl;
import ru.skillbox.socialnetwork.authentication.security.jwt.JwtAuthenticationEntryPoint;
import ru.skillbox.socialnetwork.authentication.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Order(10)
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http.securityMatcher("/api/v1/auth/**")
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().permitAll())
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authenticationManager(authenticationManager)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
//                .requestMatchers("/api/v1/auth/register", "/auth/register").permitAll()
//                .requestMatchers("/api/v1/auth/login").permitAll()
//                .requestMatchers("/api/v1/auth/change-password-link").permitAll()
//                .requestMatchers("/api/v1/auth/change-email-link").permitAll()
//                .requestMatchers("/api/v1/auth/validate").permitAll()
//                .requestMatchers("/api/v1/auth/captcha").permitAll()
//                .requestMatchers("/api/v1/auth/refresh").hasAnyRole("USER", "ADMIN")
//                .requestMatchers("/api/v1/auth/password/recovery/**").hasAnyRole("USER", "ADMIN")
//                .requestMatchers("/api/v1/auth/logout").hasAnyRole("USER", "ADMIN")
// .authenticated()
