package com.fintrust.customer.client;

import com.fintrust.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "atm-service", url = "${feign.client.atm-service.url}")
public interface AtmServiceClient {

    @PostMapping("/api/atm/internal/issue-card")
    ApiResponse<Map<String, Object>> issueCard(@RequestBody Map<String, Object> request);
}

