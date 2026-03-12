package com.fintrust.atm.controller;

import com.fintrust.atm.dto.IssueCardRequest;
import com.fintrust.atm.service.AtmService;
import com.fintrust.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/atm/internal")
@RequiredArgsConstructor
@Tag(name = "ATM Internal", description = "Internal endpoints (service-to-service)")
public class AtmInternalController {

    private final AtmService atmService;

    @PostMapping("/issue-card")
    @Operation(summary = "Issue a new card for an account (internal)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> issueCard(@Valid @RequestBody IssueCardRequest request) {
        return ResponseEntity.ok(ApiResponse.success(atmService.issueCard(request.getAccountNo())));
    }
}

