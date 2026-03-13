package com.fintrust.customer.service;

import com.fintrust.customer.dto.RegistrationRequest;
import com.fintrust.customer.entity.AccountApplication;

public interface RegistrationService {

	AccountApplication submitApplication(RegistrationRequest request);

}