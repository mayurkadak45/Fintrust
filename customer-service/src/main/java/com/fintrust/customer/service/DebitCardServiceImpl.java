package com.fintrust.customer.service;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.common.exception.BadRequestException;
import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.customer.client.AtmServiceClient;
import com.fintrust.customer.entity.Account;
import com.fintrust.customer.entity.DebitCardRequest;
import com.fintrust.customer.entity.DebitCardRequestStatus;
import com.fintrust.customer.repository.AccountRepository;
import com.fintrust.customer.repository.DebitCardRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DebitCardServiceImpl implements DebitCardService {

    private final AccountRepository accountRepository;
    private final DebitCardRequestRepository requestRepository;
    private final AtmServiceClient atmServiceClient;
    private final EmailService emailService;

    @Override
	public DebitCardRequest getLatestForUser(String username) {
        Account account = accountRepository.findAll().stream()
                .filter(a -> a.getCustomer().getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No account found for user"));
        return requestRepository.findTopByAccountNoOrderByCreatedAtDesc(account.getAccountNo()).orElse(null);
    }

    @Override
	@Transactional
    public DebitCardRequest submitRequest(String username) {
        Account account = accountRepository.findAll().stream()
                .filter(a -> a.getCustomer().getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No account found for user"));

        if (requestRepository.existsByAccountNoAndStatusIn(account.getAccountNo(), List.of(DebitCardRequestStatus.PENDING, DebitCardRequestStatus.APPROVED))) {
            throw new BadRequestException("Debit card request already exists for this account");
        }

        DebitCardRequest req = DebitCardRequest.builder()
                .accountNo(account.getAccountNo())
                .username(username)
                .email(account.getCustomer().getEmail())
                .status(DebitCardRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        req = requestRepository.save(req);

        try {
            emailService.sendDebitCardRequestReceived(req.getEmail(), account.getCustomer().getName(), req.getAccountNo());
        } catch (Exception ignored) {}

        return req;
    }

    @Override
	public List<DebitCardRequest> getPending() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(DebitCardRequestStatus.PENDING);
    }

    @Override
	@Transactional
    public DebitCardRequest approve(Long id) {
        DebitCardRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (req.getStatus() != DebitCardRequestStatus.PENDING) {
            throw new BadRequestException("Request already " + req.getStatus());
        }

        ApiResponse<Map<String, Object>> issued = atmServiceClient.issueCard(Map.of("accountNo", req.getAccountNo()));
        Map<String, Object> data = issued.getData();
        String cardNumber = data != null ? String.valueOf(data.get("cardNumber")) : null;
        String expiryDate = data != null ? String.valueOf(data.get("expiryDate")) : null;
        String pin = data != null && data.get("pin") != null ? String.valueOf(data.get("pin")) : null;

        req.setStatus(DebitCardRequestStatus.APPROVED);
        req.setApprovedCardNumber(cardNumber);
        req.setApprovedExpiryDate(expiryDate);
        req.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(req);

        try {
            emailService.sendDebitCardApproved(req.getEmail(), req.getUsername(), req.getAccountNo(), cardNumber, expiryDate, pin);
        } catch (Exception ignored) {}

        return req;
    }

    @Override
	@Transactional
    public void reject(Long id) {
        DebitCardRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (req.getStatus() != DebitCardRequestStatus.PENDING) {
            throw new BadRequestException("Request already " + req.getStatus());
        }
        req.setStatus(DebitCardRequestStatus.REJECTED);
        req.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(req);
    }
}

