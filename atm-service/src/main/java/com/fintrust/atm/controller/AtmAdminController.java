package com.fintrust.atm.controller;

import com.fintrust.atm.entity.Card;
import com.fintrust.atm.entity.CardStatus;
import com.fintrust.atm.service.AtmService;
import com.fintrust.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atm/admin")
@RequiredArgsConstructor
@Tag(name = "ATM Admin", description = "Admin APIs to manage ATM cards")
public class AtmAdminController {

    private final AtmService atmService;

    @GetMapping("/cards")
    @Operation(summary = "List all ATM cards")
    public ResponseEntity<ApiResponse<List<Card>>> listCards(@RequestParam(required = false) CardStatus status) {
        return ResponseEntity.ok(ApiResponse.success(atmService.listCards(status)));
    }

    @PostMapping("/cards/{cardNumber}/block")
    @Operation(summary = "Block a card")
    public ResponseEntity<ApiResponse<Void>> block(@PathVariable String cardNumber) {
        atmService.setCardStatus(cardNumber, CardStatus.BLOCKED);
        return ResponseEntity.ok(ApiResponse.success("Card blocked", null));
    }

    @PostMapping("/cards/{cardNumber}/unblock")
    @Operation(summary = "Unblock a card (also resets attempts)")
    public ResponseEntity<ApiResponse<Void>> unblock(@PathVariable String cardNumber) {
        atmService.unblockCard(cardNumber);
        return ResponseEntity.ok(ApiResponse.success("Card unblocked", null));
    }

    @PostMapping("/cards/{cardNumber}/reset-attempts")
    @Operation(summary = "Reset incorrect PIN attempts")
    public ResponseEntity<ApiResponse<Void>> resetAttempts(@PathVariable String cardNumber) {
        atmService.resetAttempts(cardNumber);
        return ResponseEntity.ok(ApiResponse.success("Attempts reset", null));
    }
}

