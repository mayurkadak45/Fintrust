package com.fintrust.customer.config;

import com.fintrust.customer.entity.*;
import com.fintrust.customer.repository.AccountRepository;
import com.fintrust.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            Customer john = Customer.builder()
                    .name("John Doe")
                    .email("john.doe@email.com")
                    .phone("1234567890")
                    .address("123 Main St, New York")
                    .kycStatus(KycStatus.VERIFIED)
                    .username("john.doe")
                    .build();
            john = customerRepository.save(john);

            accountRepository.save(Account.builder()
                    .accountNo("1001000001")
                    .customer(john)
                    .accountType(AccountType.SAVINGS)
                    .balance(new BigDecimal("50000.00"))
                    .build());

            accountRepository.save(Account.builder()
                    .accountNo("1001000002")
                    .customer(john)
                    .accountType(AccountType.CURRENT)
                    .balance(new BigDecimal("100000.00"))
                    .build());

            // Second customer for transfer testing
            Customer jane = Customer.builder()
                    .name("Jane Smith")
                    .email("jane.smith@email.com")
                    .phone("9876543210")
                    .address("456 Oak Ave, Boston")
                    .kycStatus(KycStatus.VERIFIED)
                    .username("jane.smith")
                    .build();
            jane = customerRepository.save(jane);

            accountRepository.save(Account.builder()
                    .accountNo("1002000001")
                    .customer(jane)
                    .accountType(AccountType.SAVINGS)
                    .balance(new BigDecimal("75000.00"))
                    .build());

            log.info("Default customers and accounts created");
        }
    }
}
