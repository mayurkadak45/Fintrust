package com.fintrust.atm.client;

import com.fintrust.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "customer-notify", url = "${feign.client.customer-service.url}")
public interface CustomerNotifyClient {

    @PostMapping("/api/accounts/internal/notify/pin-changed")
    ApiResponse<Void> pinChanged(@RequestBody Map<String, Object> request);
}

