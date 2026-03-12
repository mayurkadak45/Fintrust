package com.fintrust.customer.repository;

import com.fintrust.customer.entity.FixedDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
    List<FixedDeposit> findByAccountNoOrderByCreatedAtDesc(String accountNo);
}

