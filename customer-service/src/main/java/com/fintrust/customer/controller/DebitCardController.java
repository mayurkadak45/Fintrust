package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.entity.DebitCardRequest;
import com.fintrust.customer.service.DebitCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Debit Card", description = "Debit card request and approval APIs")
public class DebitCardController {

    private final DebitCardService debitCardService;

    @GetMapping("/api/accounts/cards/me")
    @Operation(summary = "Get latest debit card request for current user")
    public ResponseEntity<ApiResponse<DebitCardRequest>> me(@RequestHeader("X-User-Username") String username) {
        return ResponseEntity.ok(ApiResponse.success(debitCardService.getLatestForUser(username)));
    }

    @PostMapping("/api/accounts/cards/request")
    @Operation(summary = "Submit debit card request for current user")
    public ResponseEntity<ApiResponse<DebitCardRequest>> request(@RequestHeader("X-User-Username") String username) {
        return ResponseEntity.ok(ApiResponse.success("Request submitted", debitCardService.submitRequest(username)));
    }

    @GetMapping("/api/accounts/admin/cards/pending")
    @Operation(summary = "List pending debit card requests (admin)")
    public ResponseEntity<ApiResponse<List<DebitCardRequest>>> pending() {
        return ResponseEntity.ok(ApiResponse.success(debitCardService.getPending()));
    }

    @PostMapping("/api/accounts/admin/cards/{id}/approve")
    @Operation(summary = "Approve debit card request (admin)")
    public ResponseEntity<ApiResponse<DebitCardRequest>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Approved", debitCardService.approve(id)));
    }

    @PostMapping("/api/accounts/admin/cards/{id}/reject")
    @Operation(summary = "Reject debit card request (admin)")
    public ResponseEntity<ApiResponse<Void>> reject(@PathVariable Long id) {
        debitCardService.reject(id);
        return ResponseEntity.ok(ApiResponse.success("Rejected", null));
    }
}

