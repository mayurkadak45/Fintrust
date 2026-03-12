package com.fintrust.atm.repository;

import com.fintrust.atm.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    Optional<Card> findByAccountNo(String accountNo);
}
