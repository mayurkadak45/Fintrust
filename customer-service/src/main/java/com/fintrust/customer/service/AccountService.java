package com.fintrust.customer.service;

import com.fintrust.common.exception.InsufficientBalanceException;
import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.customer.dto.AccountSummaryDto;
import com.fintrust.customer.dto.BalanceDto;
import com.fintrust.customer.dto.UpdateBalanceRequest;
import com.fintrust.customer.entity.Account;
import com.fintrust.customer.entity.AccountStatus;
import com.fintrust.customer.entity.Customer;
import com.fintrust.customer.repository.AccountRepository;
import com.fintrust.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public List<AccountSummaryDto> getAccountSummary(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return accountRepository.findByCustomerId(customer.getId()).stream()
                .map(acc -> AccountSummaryDto.builder()
                        .accountNo(acc.getAccountNo())
                        .accountType(acc.getAccountType().name())
                        .balance(acc.getBalance())
                        .status(acc.getStatus().name())
                        .customerName(customer.getName())
                        .build())
                .toList();
    }

    public BalanceDto getBalance(String accountNo) {
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNo));

        return BalanceDto.builder()
                .accountNo(account.getAccountNo())
                .balance(account.getBalance())
                .build();
    }

    public boolean validateAccount(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
                .map(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                .orElse(false);
    }

    @Transactional
    public BalanceDto updateBalance(String accountNo, UpdateBalanceRequest request) {
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNo));

        if ("DEBIT".equalsIgnoreCase(request.getType())) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(request.getAmount()));
        }

        accountRepository.save(account);
        log.info("Balance updated for account {}: {} {}", accountNo, request.getType(), request.getAmount());

        return BalanceDto.builder()
                .accountNo(account.getAccountNo())
                .balance(account.getBalance())
                .build();
    }
}
