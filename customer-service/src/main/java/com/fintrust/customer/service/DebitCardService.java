package com.fintrust.customer.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.fintrust.customer.entity.DebitCardRequest;

public interface DebitCardService {

	DebitCardRequest getLatestForUser(String username);

	DebitCardRequest submitRequest(String username);

	List<DebitCardRequest> getPending();

	DebitCardRequest approve(Long id);

	void reject(Long id);

}