package com.fintrust.customer.repository;

import com.fintrust.customer.entity.DebitCardRequest;
import com.fintrust.customer.entity.DebitCardRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DebitCardRequestRepository extends JpaRepository<DebitCardRequest, Long> {
    Optional<DebitCardRequest> findTopByAccountNoOrderByCreatedAtDesc(String accountNo);
    boolean existsByAccountNoAndStatusIn(String accountNo, List<DebitCardRequestStatus> statuses);
    List<DebitCardRequest> findByStatusOrderByCreatedAtDesc(DebitCardRequestStatus status);
}

