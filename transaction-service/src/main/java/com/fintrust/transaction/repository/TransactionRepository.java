package com.fintrust.transaction.repository;

import com.fintrust.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountNoOrderByTimestampDesc(String accountNo);
    List<Transaction> findTop10ByAccountNoOrderByTimestampDesc(String accountNo);
    List<Transaction> findAllByOrderByTimestampDesc();
}
