package com.fintrust.transaction.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.transaction.dto.*;
import com.fintrust.transaction.entity.FundTransfer;
import com.fintrust.transaction.entity.Transaction;
import com.fintrust.transaction.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transfers", description = "Fund transfer and transaction APIs")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Execute fund transfer")
    public ResponseEntity<ApiResponse<FundTransfer>> transfer(@Valid @RequestBody TransferRequest request) {
        FundTransfer transfer = transferService.executeTransfer(request);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", transfer));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate transfer accounts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateTransfer(
            @Valid @RequestBody TransferValidationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(transferService.validateTransfer(request)));
    }

    @GetMapping("/history")
    @Operation(summary = "Get transaction history")
    public ResponseEntity<ApiResponse<List<TransactionHistoryDto>>> getHistory(
            @RequestParam String accountNo) {
        return ResponseEntity.ok(ApiResponse.success(transferService.getTransactionHistory(accountNo)));
    }

    @GetMapping("/mini-statement/{accountNo}")
    @Operation(summary = "Get mini statement (last 10 transactions)")
    public ResponseEntity<ApiResponse<List<TransactionHistoryDto>>> getMiniStatement(
            @PathVariable String accountNo) {
        return ResponseEntity.ok(ApiResponse.success(transferService.getMiniStatement(accountNo)));
    }

    @PostMapping("/atm-transaction")
    @Operation(summary = "Create ATM transaction (internal)")
    public ResponseEntity<ApiResponse<Transaction>> createAtmTransaction(
            @RequestBody AtmTransactionRequest request) {
        Transaction txn = transferService.createAtmTransaction(request);
        return ResponseEntity.ok(ApiResponse.success(txn));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all transactions (admin)")
    public ResponseEntity<ApiResponse<List<TransactionHistoryDto>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.success(transferService.getAllTransactions()));
    }
}
