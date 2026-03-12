package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.dto.AccountSummaryDto;
import com.fintrust.customer.dto.BalanceDto;
import com.fintrust.customer.dto.UpdateBalanceRequest;
import com.fintrust.customer.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management APIs")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/summary")
    @Operation(summary = "Get account summary for authenticated user")
    public ResponseEntity<ApiResponse<List<AccountSummaryDto>>> getAccountSummary(
            @RequestHeader("X-User-Username") String username) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getAccountSummary(username)));
    }

    @GetMapping("/{accountNo}/balance")
    @Operation(summary = "Get account balance")
    public ResponseEntity<ApiResponse<BalanceDto>> getBalance(@PathVariable String accountNo) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getBalance(accountNo)));
    }

    @GetMapping("/{accountNo}/validate")
    @Operation(summary = "Validate account (internal)")
    public ResponseEntity<ApiResponse<Boolean>> validateAccount(@PathVariable String accountNo) {
        return ResponseEntity.ok(ApiResponse.success(accountService.validateAccount(accountNo)));
    }

    @PutMapping("/{accountNo}/balance")
    @Operation(summary = "Update account balance (internal)")
    public ResponseEntity<ApiResponse<BalanceDto>> updateBalance(
            @PathVariable String accountNo,
            @RequestBody UpdateBalanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(accountService.updateBalance(accountNo, request)));
    }
}
