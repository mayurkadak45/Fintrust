package com.fintrust.atm.controller;

import com.fintrust.atm.dto.*;
import com.fintrust.atm.service.AtmService;
import com.fintrust.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/atm")
@RequiredArgsConstructor
@Tag(name = "ATM", description = "Virtual ATM Simulation APIs")
public class AtmController {

    private final AtmService atmService;

    @PostMapping("/validate-card")
    @Operation(summary = "Validate ATM card and create session")
    public ResponseEntity<ApiResponse<AtmSessionDto>> validateCard(
            @Valid @RequestBody CardValidationRequest request) {
        AtmSessionDto session = atmService.validateCard(request);
        return ResponseEntity.ok(ApiResponse.success("Card validated. Please enter PIN.", session));
    }

    @PostMapping("/validate-pin")
    @Operation(summary = "Validate PIN for ATM session")
    public ResponseEntity<ApiResponse<AtmSessionDto>> validatePin(
            @Valid @RequestBody PinValidationRequest request) {
        AtmSessionDto session = atmService.validatePin(request);
        return ResponseEntity.ok(ApiResponse.success("PIN verified successfully", session));
    }

    @GetMapping("/balance")
    @Operation(summary = "Check account balance via ATM")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalance(
            @RequestParam String sessionToken) {
        return ResponseEntity.ok(ApiResponse.success(atmService.getBalance(sessionToken)));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw cash from ATM")
    public ResponseEntity<ApiResponse<Map<String, Object>>> withdraw(
            @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", atmService.withdraw(request)));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit cash at ATM")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deposit(
            @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Deposit successful", atmService.deposit(request)));
    }

    @PostMapping("/change-pin")
    @Operation(summary = "Change ATM PIN (validate online banking credentials + account)")
    public ResponseEntity<ApiResponse<Void>> changePin(@Valid @RequestBody ChangePinRequest request) {
        atmService.changePin(request);
        return ResponseEntity.ok(ApiResponse.success("PIN changed successfully", null));
    }

    @GetMapping("/mini-statement")
    @Operation(summary = "Get mini statement via ATM")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMiniStatement(
            @RequestParam String sessionToken) {
        return ResponseEntity.ok(ApiResponse.success(atmService.getMiniStatement(sessionToken)));
    }

    @PostMapping("/end-session")
    @Operation(summary = "End ATM session")
    public ResponseEntity<ApiResponse<Void>> endSession(@RequestParam String sessionToken) {
        atmService.endSession(sessionToken);
        return ResponseEntity.ok(ApiResponse.success("Session ended. Please collect your card.", null));
    }
}
