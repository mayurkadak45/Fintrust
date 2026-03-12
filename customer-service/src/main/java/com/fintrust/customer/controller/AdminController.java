package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.dto.CustomerDetailDto;
import com.fintrust.customer.dto.DashboardStatsDto;
import com.fintrust.customer.entity.AccountApplication;
import com.fintrust.customer.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending account applications")
    public ResponseEntity<ApiResponse<List<AccountApplication>>> getPending() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getPendingApplications()));
    }

    @GetMapping("/pending/{id}")
    @Operation(summary = "Get single pending application details")
    public ResponseEntity<ApiResponse<AccountApplication>> getPendingById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getApplicationById(id)));
    }

    @PostMapping("/pending/{id}/approve")
    @Operation(summary = "Approve a pending application")
    public ResponseEntity<ApiResponse<CustomerDetailDto>> approve(@PathVariable Long id) {
        CustomerDetailDto result = adminService.approveApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Application approved. Account created.", result));
    }

    @PostMapping("/pending/{id}/reject")
    @Operation(summary = "Reject a pending application")
    public ResponseEntity<ApiResponse<Void>> reject(@PathVariable Long id) {
        adminService.rejectApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Application rejected", null));
    }

    @GetMapping("/customers")
    @Operation(summary = "Get all customers with accounts")
    public ResponseEntity<ApiResponse<List<CustomerDetailDto>>> getCustomers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllCustomers()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers by name or account number")
    public ResponseEntity<ApiResponse<List<CustomerDetailDto>>> search(@RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(ApiResponse.success(adminService.searchCustomers(q)));
    }
}
