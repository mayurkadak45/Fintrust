package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.dto.BeneficiaryDto;
import com.fintrust.customer.service.BeneficiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
@Tag(name = "Beneficiaries", description = "Beneficiary management APIs")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    @Operation(summary = "Get all beneficiaries for authenticated user")
    public ResponseEntity<ApiResponse<List<BeneficiaryDto>>> getBeneficiaries(
            @RequestHeader("X-User-Username") String username) {
        return ResponseEntity.ok(ApiResponse.success(beneficiaryService.getBeneficiaries(username)));
    }

    @PostMapping
    @Operation(summary = "Add a new beneficiary")
    public ResponseEntity<ApiResponse<BeneficiaryDto>> addBeneficiary(
            @RequestHeader("X-User-Username") String username,
            @Valid @RequestBody BeneficiaryDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Beneficiary added", beneficiaryService.addBeneficiary(username, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a beneficiary")
    public ResponseEntity<ApiResponse<Void>> deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary deleted", null));
    }
}
