package com.fintrust.customer.service;

import com.fintrust.common.exception.BadRequestException;
import com.fintrust.customer.dto.RegistrationRequest;
import com.fintrust.customer.entity.AccountApplication;
import com.fintrust.customer.entity.AccountType;
import com.fintrust.customer.repository.AccountApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private final AccountApplicationRepository applicationRepository;
    private final EmailService emailService;

    public AccountApplication submitApplication(RegistrationRequest request) {
        if (applicationRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An application with this email already exists");
        }

        AccountType accountType;
        try {
            accountType = AccountType.valueOf(request.getAccountType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid account type: " + request.getAccountType());
        }

        AccountApplication application = AccountApplication.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .aadhaar(request.getAadhaar())
                .pan(request.getPan())
                .aadhaarPhotoUrl(request.getAadhaarPhotoUrl())
                .panPhotoUrl(request.getPanPhotoUrl())
                .passportPhotoUrl(request.getPassportPhotoUrl())
                .accountType(accountType)
                .address(request.getAddress())
                .build();

        application = applicationRepository.save(application);
        log.info("New account application submitted: {} ({})", request.getFullName(), request.getEmail());

        try {
            emailService.sendApplicationReceived(request.getEmail(), request.getFullName());
        } catch (Exception e) {
            log.warn("Failed to send application-received email: {}", e.getMessage());
        }
        return application;
    }
}
