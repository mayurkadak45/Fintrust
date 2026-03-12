package com.fintrust.customer.service;

import com.fintrust.common.exception.BadRequestException;
import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.customer.dto.CreateFixedDepositRequest;
import com.fintrust.customer.entity.Account;
import com.fintrust.customer.entity.FixedDeposit;
import com.fintrust.customer.repository.AccountRepository;
import com.fintrust.customer.repository.FixedDepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FixedDepositService {

    private final AccountRepository accountRepository;
    private final FixedDepositRepository fdRepository;

    private static final Map<Integer, BigDecimal> RATES = Map.of(
            2, new BigDecimal("5.00"),
            3, new BigDecimal("5.50"),
            5, new BigDecimal("6.50"),
            10, new BigDecimal("7.50")
    );

    private Account getAccountForUser(String username) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getCustomer().getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No account found for user"));
    }

    public List<FixedDeposit> list(String username) {
        Account acc = getAccountForUser(username);
        return fdRepository.findByAccountNoOrderByCreatedAtDesc(acc.getAccountNo());
    }

    @Transactional
    public FixedDeposit create(String username, CreateFixedDepositRequest request) {
        Account acc = getAccountForUser(username);

        if (request.getAmount() == null || request.getAmount().compareTo(new BigDecimal("1000")) < 0) {
            throw new BadRequestException("Minimum FD amount is 1000");
        }
        BigDecimal rate = RATES.get(request.getYears());
        if (rate == null) {
            throw new BadRequestException("Supported years are 2, 3, 5, 10");
        }

        LocalDate start = LocalDate.now();
        LocalDate maturity = start.plusYears(request.getYears());

        BigDecimal maturityAmount = request.getAmount()
                .multiply(BigDecimal.ONE.add(rate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).multiply(new BigDecimal(request.getYears()))))
                .setScale(2, RoundingMode.HALF_UP);

        FixedDeposit fd = FixedDeposit.builder()
                .accountNo(acc.getAccountNo())
                .principal(request.getAmount().setScale(2, RoundingMode.HALF_UP))
                .years(request.getYears())
                .interestRate(rate)
                .startDate(start)
                .maturityDate(maturity)
                .maturityAmount(maturityAmount)
                .build();

        return fdRepository.save(fd);
    }
}

