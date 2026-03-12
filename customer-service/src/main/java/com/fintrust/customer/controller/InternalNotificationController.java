package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.customer.dto.InternalAtmNotifyRequest;
import com.fintrust.customer.dto.InternalPinChangedRequest;
import com.fintrust.customer.entity.Account;
import com.fintrust.customer.repository.AccountRepository;
import com.fintrust.customer.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/accounts/internal/notify")
@RequiredArgsConstructor
@Tag(name = "Internal Notifications", description = "Internal service-to-service notification endpoints")
public class InternalNotificationController {

    private final AccountRepository accountRepository;
    private final EmailService emailService;

    private Account getAccount(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNo));
    }

    @PostMapping("/pin-changed")
    @Operation(summary = "Send PIN changed email (internal)")
    public ResponseEntity<ApiResponse<Void>> pinChanged(@RequestBody InternalPinChangedRequest req) {
        Account acc = getAccount(req.getAccountNo());
        emailService.sendAtmPinChanged(acc.getCustomer().getEmail(), acc.getCustomer().getName(), acc.getAccountNo(), req.getCardMasked());
        return ResponseEntity.ok(ApiResponse.success("OK", null));
    }

    @PostMapping("/atm-transaction")
    @Operation(summary = "Send ATM withdraw/deposit email (internal)")
    public ResponseEntity<ApiResponse<Void>> atmTxn(@RequestBody InternalAtmNotifyRequest req) {
        Account acc = getAccount(req.getAccountNo());
        String amount = req.getAmount() != null ? "₹" + req.getAmount().toPlainString() : "-";
        String bal = req.getBalanceAfter() != null ? "₹" + req.getBalanceAfter().toPlainString() : "-";
        String txnId = req.getTxnId() != null ? String.valueOf(req.getTxnId()) : "-";
        String ts = req.getTimestamp() != null ? req.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "-";
        emailService.sendAtmTransactionAlert(
                acc.getCustomer().getEmail(),
                acc.getCustomer().getName(),
                acc.getAccountNo(),
                req.getType(),
                amount,
                bal,
                txnId,
                ts
        );
        return ResponseEntity.ok(ApiResponse.success("OK", null));
    }
}

