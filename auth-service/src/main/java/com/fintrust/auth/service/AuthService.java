package com.fintrust.auth.service;

import com.fintrust.auth.dto.ChangePasswordRequest;
import com.fintrust.auth.dto.CreateCustomerUserRequest;
import com.fintrust.auth.dto.LoginRequest;
import com.fintrust.auth.dto.LoginResponse;
import com.fintrust.auth.entity.User;
import com.fintrust.auth.entity.UserStatus;
import com.fintrust.auth.repository.UserRepository;
import com.fintrust.common.exception.AccountLockedException;
import com.fintrust.common.exception.BadRequestException;
import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.common.exception.UnauthorizedException;
import com.fintrust.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new AccountLockedException("Account is locked due to too many failed login attempts");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setStatus(UserStatus.LOCKED);
                log.warn("Account locked for user: {}", user.getUsername());
            }
            userRepository.save(user);
            throw new UnauthorizedException("Invalid credentials");
        }

        // Reset failed attempts on successful login
        user.setFailedAttempts(0);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        log.info("User logged in: {}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }

    @Transactional
    public void createCustomerUser(CreateCustomerUserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
            log.info("User {} already exists, skipping creation", existing.getUsername());
        });

        if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(com.fintrust.auth.entity.Role.CUSTOMER)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(user);
            log.info("Customer user created: {}", request.getUsername());
        }
    }
}
