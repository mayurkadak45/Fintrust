package com.fintrust.transaction.client;

import com.fintrust.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "account-service", url = "${feign.client.account-service.url}")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountNo}/validate")
    ApiResponse<Boolean> validateAccount(@PathVariable("accountNo") String accountNo);

    @GetMapping("/api/accounts/{accountNo}/balance")
    ApiResponse<Map<String, Object>> getBalance(@PathVariable("accountNo") String accountNo);

    @PutMapping("/api/accounts/{accountNo}/balance")
    ApiResponse<Map<String, Object>> updateBalance(
            @PathVariable("accountNo") String accountNo,
            @RequestBody Map<String, Object> request);
}
