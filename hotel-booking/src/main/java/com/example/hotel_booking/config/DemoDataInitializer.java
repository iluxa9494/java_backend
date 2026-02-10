package com.example.hotel_booking.config;

import com.example.hotel_booking.model.Role;
import com.example.hotel_booking.model.RoleType;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.jpa.RoleRepository;
import com.example.hotel_booking.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("demo")
@RequiredArgsConstructor
@Slf4j
public class DemoDataInitializer {
    private static final String DEMO_USERNAME = "demo-user";
    private static final String DEMO_EMAIL = "demo@hotel.local";
    private static final String DEMO_PASSWORD = "demo1234";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner demoDataRunner() {
        return args -> {
            Role role = roleRepository.findByName(RoleType.USER)
                    .orElseGet(() -> {
                        Role created = Role.builder().name(RoleType.USER).build();
                        Role saved = roleRepository.save(created);
                        log.info("Demo: created USER role with id={}", saved.getId());
                        return saved;
                    });

            if (userRepository.existsByUsername(DEMO_USERNAME) || userRepository.existsByEmail(DEMO_EMAIL)) {
                log.info("Demo: user already exists (username/email).");
                return;
            }

            User user = User.builder()
                    .username(DEMO_USERNAME)
                    .email(DEMO_EMAIL)
                    .password(passwordEncoder.encode(DEMO_PASSWORD))
                    .role(role)
                    .build();
            User saved = userRepository.save(user);
            log.info("Demo: created user id={}, username={}, email={}", saved.getId(), saved.getUsername(), saved.getEmail());
        };
    }
}
