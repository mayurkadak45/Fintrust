package com.fintrust.customer.repository;

import com.fintrust.customer.entity.AccountApplication;
import com.fintrust.customer.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountApplicationRepository extends JpaRepository<AccountApplication, Long> {
    List<AccountApplication> findByStatusOrderByCreatedAtDesc(ApplicationStatus status);
    boolean existsByEmail(String email);
}
