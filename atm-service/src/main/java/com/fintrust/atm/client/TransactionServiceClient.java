package com.fintrust.atm.client;

import com.fintrust.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "transaction-service", url = "${feign.client.transaction-service.url}")
public interface TransactionServiceClient {

    @PostMapping("/api/transfers/atm-transaction")
    ApiResponse<Map<String, Object>> createAtmTransaction(@RequestBody Map<String, Object> request);

    @GetMapping("/api/transfers/mini-statement/{accountNo}")
    ApiResponse<List<Map<String, Object>>> getMiniStatement(@PathVariable("accountNo") String accountNo);
}
