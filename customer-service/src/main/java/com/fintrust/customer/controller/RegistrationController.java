package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.dto.RegistrationRequest;
import com.fintrust.customer.entity.AccountApplication;
import com.fintrust.customer.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Registration", description = "Customer account registration APIs")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    @Operation(summary = "Submit new account application")
    public ResponseEntity<ApiResponse<AccountApplication>> register(
            @Valid @RequestBody RegistrationRequest request) {
        AccountApplication application = registrationService.submitApplication(request);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully. Pending admin approval.", application));
    }
}
