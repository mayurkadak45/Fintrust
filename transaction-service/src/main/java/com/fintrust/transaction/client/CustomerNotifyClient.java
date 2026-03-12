package com.fintrust.transaction.client;

import com.fintrust.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "customer-notify", url = "${feign.client.customer-service.url}")
public interface CustomerNotifyClient {

    @PostMapping("/api/accounts/internal/notify/atm-transaction")
    ApiResponse<Void> atmTxn(@RequestBody Map<String, Object> request);
}

