package com.fintrust.customer.client;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.dto.CreateCustomerUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${feign.client.auth-service.url:http://localhost:8081}")
public interface AuthServiceClient {

    @PostMapping("/api/auth/internal/customers")
    ApiResponse<Void> createCustomerUser(@RequestBody CreateCustomerUserRequest request);
}

