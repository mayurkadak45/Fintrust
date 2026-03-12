package com.fintrust.atm.client;

import com.fintrust.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${feign.client.auth-service.url}")
public interface AuthServiceClient {

    @PostMapping("/api/auth/login")
    ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> request);
}

