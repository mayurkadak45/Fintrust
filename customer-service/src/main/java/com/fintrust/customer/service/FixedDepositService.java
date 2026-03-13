package com.fintrust.customer.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.fintrust.customer.dto.CreateFixedDepositRequest;
import com.fintrust.customer.entity.FixedDeposit;

public interface FixedDepositService {

	List<FixedDeposit> list(String username);

	FixedDeposit create(String username, CreateFixedDepositRequest request);

}