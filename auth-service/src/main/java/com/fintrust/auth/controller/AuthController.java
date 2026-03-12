package com.fintrust.auth.controller;

import com.fintrust.auth.dto.ChangePasswordRequest;
import com.fintrust.auth.dto.CreateCustomerUserRequest;
import com.fintrust.auth.dto.LoginRequest;
import com.fintrust.auth.dto.LoginResponse;
import com.fintrust.auth.service.AuthService;
import com.fintrust.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout current user")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping("/internal/customers")
    @Operation(summary = "Create customer user (internal)", description = "Internal endpoint used by customer-service when approving applications")
    public ResponseEntity<ApiResponse<Void>> createCustomerUser(@Valid @RequestBody CreateCustomerUserRequest request) {
        authService.createCustomerUser(request);
        return ResponseEntity.ok(ApiResponse.success("Customer user created", null));
    }
}
