package com.fintrust.customer.service;

import java.util.List;

import com.fintrust.customer.dto.AccountSummaryDto;
import com.fintrust.customer.dto.BalanceDto;
import com.fintrust.customer.dto.UpdateBalanceRequest;

public interface AccountService {

	List<AccountSummaryDto> getAccountSummary(String username);

	BalanceDto getBalance(String accountNo);

	boolean validateAccount(String accountNo);

	BalanceDto updateBalance(String accountNo, UpdateBalanceRequest request);

}