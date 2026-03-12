package com.fintrust.transaction.service;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.common.exception.BadRequestException;
import com.fintrust.transaction.client.AccountServiceClient;
import com.fintrust.transaction.client.CustomerNotifyClient;
import com.fintrust.transaction.dto.*;
import com.fintrust.transaction.entity.*;
import com.fintrust.transaction.repository.FundTransferRepository;
import com.fintrust.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);
    private final TransactionRepository transactionRepository;
    private final FundTransferRepository fundTransferRepository;
    private final AccountServiceClient accountServiceClient;
    private final CustomerNotifyClient customerNotifyClient;

    public Map<String, Object> validateTransfer(TransferValidationRequest request) {
        ApiResponse<Boolean> srcValid = accountServiceClient.validateAccount(request.getSrcAccount());
        ApiResponse<Boolean> destValid = accountServiceClient.validateAccount(request.getDestAccount());

        boolean valid = Boolean.TRUE.equals(srcValid.getData()) && Boolean.TRUE.equals(destValid.getData());

        return Map.of(
                "valid", valid,
                "srcAccountValid", Boolean.TRUE.equals(srcValid.getData()),
                "destAccountValid", Boolean.TRUE.equals(destValid.getData())
        );
    }

    @Transactional
    public FundTransfer executeTransfer(TransferRequest request) {
        // Validate both accounts
        ApiResponse<Boolean> srcValid = accountServiceClient.validateAccount(request.getSrcAccount());
        if (!Boolean.TRUE.equals(srcValid.getData())) {
            throw new BadRequestException("Source account is invalid or inactive");
        }

        ApiResponse<Boolean> destValid = accountServiceClient.validateAccount(request.getDestAccount());
        if (!Boolean.TRUE.equals(destValid.getData())) {
            throw new BadRequestException("Destination account is invalid or inactive");
        }

        // Debit source account
        Map<String, Object> debitReq = Map.of("amount", request.getAmount(), "type", "DEBIT");
        accountServiceClient.updateBalance(request.getSrcAccount(), debitReq);

        // Credit destination account
        Map<String, Object> creditReq = Map.of("amount", request.getAmount(), "type", "CREDIT");
        accountServiceClient.updateBalance(request.getDestAccount(), creditReq);

        // Record the fund transfer
        FundTransfer transfer = FundTransfer.builder()
                .srcAccount(request.getSrcAccount())
                .destAccount(request.getDestAccount())
                .amount(request.getAmount())
                .remarks(request.getRemarks())
                .status(TransactionStatus.SUCCESS)
                .build();
        transfer = fundTransferRepository.save(transfer);

        // Record transactions for both accounts
        transactionRepository.save(Transaction.builder()
                .accountNo(request.getSrcAccount())
                .type(TransactionType.DEBIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description("Transfer to " + request.getDestAccount())
                .build());

        transactionRepository.save(Transaction.builder()
                .accountNo(request.getDestAccount())
                .type(TransactionType.CREDIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description("Transfer from " + request.getSrcAccount())
                .build());

        log.info("Fund transfer completed: {} -> {} amount: {}",
                request.getSrcAccount(), request.getDestAccount(), request.getAmount());

        return transfer;
    }

    public List<TransactionHistoryDto> getTransactionHistory(String accountNo) {
        return transactionRepository.findByAccountNoOrderByTimestampDesc(accountNo).stream()
                .map(this::toHistoryDto)
                .toList();
    }

    public List<TransactionHistoryDto> getMiniStatement(String accountNo) {
        return transactionRepository.findTop10ByAccountNoOrderByTimestampDesc(accountNo).stream()
                .map(this::toHistoryDto)
                .toList();
    }

    @Transactional
    public Transaction createAtmTransaction(AtmTransactionRequest request) {
        String balanceType = "WITHDRAWAL".equalsIgnoreCase(request.getType()) ? "DEBIT" : "CREDIT";

        Map<String, Object> balanceReq = Map.of(
                "amount", request.getAmount(),
                "type", balanceType
        );
        ApiResponse<Map<String, Object>> balRes = accountServiceClient.updateBalance(request.getAccountNo(), balanceReq);

        Object balObj = balRes != null && balRes.getData() != null ? balRes.getData().get("balance") : null;
        java.math.BigDecimal balanceAfter = null;
        if (balObj != null) {
            balanceAfter = new java.math.BigDecimal(balObj.toString());
        }

        Transaction txn = Transaction.builder()
                .accountNo(request.getAccountNo())
                .type("WITHDRAWAL".equalsIgnoreCase(request.getType()) ? TransactionType.DEBIT : TransactionType.CREDIT)
                .amount(request.getAmount())
                .balanceAfter(balanceAfter)
                .status(TransactionStatus.SUCCESS)
                .description("ATM " + request.getType())
                .build();

        txn = transactionRepository.save(txn);
        log.info("ATM transaction created: {} {} {}", request.getAccountNo(), request.getType(), request.getAmount());

        try {
            Object bal = balRes != null && balRes.getData() != null ? balRes.getData().get("balance") : null;
            customerNotifyClient.atmTxn(Map.of(
                    "accountNo", request.getAccountNo(),
                    "type", request.getType(),
                    "amount", request.getAmount(),
                    "balanceAfter", bal,
                    "txnId", txn.getTxnId(),
                    "timestamp", txn.getTimestamp()
            ));
        } catch (Exception ex) {
            log.warn("ATM txn email notify failed: {}", ex.getMessage());
        }

        return txn;
    }

    public List<TransactionHistoryDto> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::toHistoryDto)
                .toList();
    }

    private TransactionHistoryDto toHistoryDto(Transaction txn) {
        return TransactionHistoryDto.builder()
                .txnId(txn.getTxnId())
                .accountNo(txn.getAccountNo())
                .type(txn.getType().name())
                .amount(txn.getAmount())
                .balanceAfter(txn.getBalanceAfter())
                .status(txn.getStatus().name())
                .timestamp(txn.getTimestamp())
                .description(txn.getDescription())
                .build();
    }
}
