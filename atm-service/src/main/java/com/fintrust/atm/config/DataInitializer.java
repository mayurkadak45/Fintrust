package com.fintrust.atm.config;

import com.fintrust.atm.entity.AtmMachine;
import com.fintrust.atm.entity.Card;
import com.fintrust.atm.repository.AtmMachineRepository;
import com.fintrust.atm.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CardRepository cardRepository;
    private final AtmMachineRepository atmMachineRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (cardRepository.count() == 0) {
            cardRepository.save(Card.builder()
                    .cardNumber("4111111111111111")
                    .accountNo("1001000001")
                    .pinHash(passwordEncoder.encode("1234"))
                    .expiryDate(LocalDate.of(2028, 12, 31))
                    .build());

            cardRepository.save(Card.builder()
                    .cardNumber("4222222222222222")
                    .accountNo("1001000002")
                    .pinHash(passwordEncoder.encode("5678"))
                    .expiryDate(LocalDate.of(2028, 12, 31))
                    .build());

            cardRepository.save(Card.builder()
                    .cardNumber("4333333333333333")
                    .accountNo("1002000001")
                    .pinHash(passwordEncoder.encode("9012"))
                    .expiryDate(LocalDate.of(2028, 12, 31))
                    .build());

            log.info("Default cards created: 4111...1111 (PIN: 1234), 4222...2222 (PIN: 5678), 4333...3333 (PIN: 9012)");
        }

        if (atmMachineRepository.count() == 0) {
            atmMachineRepository.save(AtmMachine.builder()
                    .location("Main Branch - 123 Main St, New York")
                    .build());

            atmMachineRepository.save(AtmMachine.builder()
                    .location("Airport Terminal - JFK Airport, New York")
                    .build());

            log.info("Default ATM machines created");
        }
    }
}
