package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.dto.CreateFixedDepositRequest;
import com.fintrust.customer.entity.FixedDeposit;
import com.fintrust.customer.service.FixedDepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/fd")
@RequiredArgsConstructor
@Tag(name = "Fixed Deposits", description = "Fixed deposit APIs")
public class FixedDepositController {

    private final FixedDepositService fixedDepositService;

    @GetMapping
    @Operation(summary = "List fixed deposits for current user")
    public ResponseEntity<ApiResponse<List<FixedDeposit>>> list(@RequestHeader("X-User-Username") String username) {
        return ResponseEntity.ok(ApiResponse.success(fixedDepositService.list(username)));
    }

    @PostMapping
    @Operation(summary = "Create fixed deposit for current user")
    public ResponseEntity<ApiResponse<FixedDeposit>> create(
            @RequestHeader("X-User-Username") String username,
            @Valid @RequestBody CreateFixedDepositRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("FD created", fixedDepositService.create(username, request)));
    }
}

