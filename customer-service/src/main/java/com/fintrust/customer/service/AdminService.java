package com.fintrust.customer.service;

import com.fintrust.common.exception.BadRequestException;
import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.customer.client.AuthServiceClient;
import com.fintrust.customer.dto.CreateCustomerUserRequest;
import com.fintrust.customer.dto.CustomerDetailDto;
import com.fintrust.customer.dto.DashboardStatsDto;
import com.fintrust.customer.entity.*;
import com.fintrust.customer.repository.AccountApplicationRepository;
import com.fintrust.customer.repository.AccountRepository;
import com.fintrust.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountApplicationRepository applicationRepository;
    private final AuthServiceClient authServiceClient;
    private final EmailService emailService;

    public DashboardStatsDto getDashboardStats() {
        long totalCustomers = customerRepository.count();
        long pendingAccounts = applicationRepository.findByStatusOrderByCreatedAtDesc(ApplicationStatus.PENDING).size();
        long totalAccounts = accountRepository.count();
        BigDecimal bankBalance = accountRepository.findAll().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardStatsDto.builder()
                .totalCustomers(totalCustomers)
                .pendingAccounts(pendingAccounts)
                .totalTransactions(totalAccounts)
                .bankBalance(bankBalance)
                .build();
    }

    public List<AccountApplication> getPendingApplications() {
        return applicationRepository.findByStatusOrderByCreatedAtDesc(ApplicationStatus.PENDING);
    }

    public AccountApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    @Transactional
    public CustomerDetailDto approveApplication(Long id) {
        AccountApplication app = getApplicationById(id);
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Application is already " + app.getStatus());
        }

        // Create customer
        String username = app.getEmail().split("@")[0].toLowerCase()
                + new Random().nextInt(100);

        Customer customer = Customer.builder()
                .name(app.getFullName())
                .email(app.getEmail())
                .phone(app.getMobile())
                .address(app.getAddress())
                .kycStatus(KycStatus.VERIFIED)
                .username(username)
                .build();
        customer = customerRepository.save(customer);

        // Generate account number
        String accountNo = "50" + String.format("%010d", System.currentTimeMillis() % 10000000000L);

        Account account = Account.builder()
                .accountNo(accountNo)
                .customer(customer)
                .accountType(app.getAccountType())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .build();
        account = accountRepository.save(account);

        // Generate a temporary login password and create auth user via auth-service
        String tempPassword = "FT" + String.format("%04d", new Random().nextInt(10000));
        try {
            authServiceClient.createCustomerUser(CreateCustomerUserRequest.builder()
                    .username(username)
                    .password(tempPassword)
                    .email(customer.getEmail())
                    .build());
        } catch (Exception ex) {
            log.error("Failed to create auth user for customer {}: {}", username, ex.getMessage());
        }

        // Mark application approved
        app.setStatus(ApplicationStatus.APPROVED);
        applicationRepository.save(app);

        try {
            emailService.sendApplicationApproved(customer.getEmail(), customer.getName(), username, tempPassword, accountNo);
        } catch (Exception e) {
            log.warn("Failed to send approval email: {}", e.getMessage());
        }

        log.info("Application approved: {} -> account {}", app.getFullName(), accountNo);

        return CustomerDetailDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .accountNo(account.getAccountNo())
                .email(customer.getEmail())
                .mobile(customer.getPhone())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .username(username)
                .temporaryPassword(tempPassword)
                .build();
    }

    @Transactional
    public void rejectApplication(Long id) {
        AccountApplication app = getApplicationById(id);
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Application is already " + app.getStatus());
        }
        app.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(app);
        log.info("Application rejected: {}", app.getFullName());
    }

    public List<CustomerDetailDto> getAllCustomers() {
        return accountRepository.findAll().stream()
                .map(acc -> CustomerDetailDto.builder()
                        .id(acc.getCustomer().getId())
                        .name(acc.getCustomer().getName())
                        .accountNo(acc.getAccountNo())
                        .email(acc.getCustomer().getEmail())
                        .mobile(acc.getCustomer().getPhone())
                        .accountType(acc.getAccountType().name())
                        .balance(acc.getBalance())
                        .status(acc.getStatus().name())
                        .createdAt(acc.getCreatedAt() != null
                                ? acc.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                                : null)
                        .username(acc.getCustomer().getUsername())
                        .temporaryPassword(null)
                        .build())
                .toList();
    }

    public List<CustomerDetailDto> searchCustomers(String query) {
        if (query == null || query.isBlank()) {
            return getAllCustomers();
        }
        String q = query.toLowerCase();
        return getAllCustomers().stream()
                .filter(c -> c.getName().toLowerCase().contains(q)
                        || c.getAccountNo().contains(q))
                .toList();
    }
}
