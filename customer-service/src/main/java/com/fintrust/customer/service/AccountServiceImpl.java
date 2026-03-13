package com.fintrust.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
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

    @Override
	public BalanceDto getBalance(String accountNo) {
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNo));

        return BalanceDto.builder()
                .accountNo(account.getAccountNo())
                .balance(account.getBalance())
                .build();
    }

    @Override
	public boolean validateAccount(String accountNo) {
        return accountRepository.findByAccountNo(accountNo)
                .map(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                .orElse(false);
    }

    @Override
	@Transactional
    public BalanceDto updateBalance(String accountNo, UpdateBalanceRequest request) {
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNo));

        if ("DEBIT".equalsIgnoreCase(request.getType())) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(request.getAmount()));
            //RBI API
        } else {
            account.setBalance(account.getBalance().add(request.getAmount()));
            //RBI API
        }

        accountRepository.save(account);
        log.info("Balance updated for account {}: {} {}", accountNo, request.getType(), request.getAmount());

        return BalanceDto.builder()
                .accountNo(account.getAccountNo())
                .balance(account.getBalance())
                .build();
    }
}
