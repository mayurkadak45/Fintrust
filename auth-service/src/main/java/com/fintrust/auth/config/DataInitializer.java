package com.fintrust.auth.config;

import com.fintrust.auth.entity.Role;
import com.fintrust.auth.entity.User;
import com.fintrust.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);

            User customer = User.builder()
                    .username("john.doe")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.CUSTOMER)
                    .build();
            userRepository.save(customer);

            log.info("Default users created: admin (admin123), john.doe (password123)");
        }
    }
}
