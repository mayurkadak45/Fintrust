package com.fintrust.customer.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.fintrust.customer.dto.CustomerDetailDto;
import com.fintrust.customer.dto.DashboardStatsDto;
import com.fintrust.customer.entity.AccountApplication;

public interface AdminService {

	DashboardStatsDto getDashboardStats();

	List<AccountApplication> getPendingApplications();

	AccountApplication getApplicationById(Long id);

	CustomerDetailDto approveApplication(Long id);

	void rejectApplication(Long id);

	List<CustomerDetailDto> getAllCustomers();

	List<CustomerDetailDto> searchCustomers(String query);

}