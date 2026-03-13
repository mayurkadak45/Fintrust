package com.fintrust.customer.service;

import java.util.List;

import com.fintrust.customer.dto.BeneficiaryDto;

public interface BeneficiaryService {

	List<BeneficiaryDto> getBeneficiaries(String username);

	BeneficiaryDto addBeneficiary(String username, BeneficiaryDto dto);

	void deleteBeneficiary(Long id);

}