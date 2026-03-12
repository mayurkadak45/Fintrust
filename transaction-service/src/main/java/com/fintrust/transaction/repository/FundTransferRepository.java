package com.fintrust.transaction.repository;

import com.fintrust.transaction.entity.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {
    List<FundTransfer> findBySrcAccountOrDestAccountOrderByTimestampDesc(String src, String dest);
}
