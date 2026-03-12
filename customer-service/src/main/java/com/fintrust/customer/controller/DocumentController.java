package com.fintrust.customer.controller;

import com.fintrust.common.dto.ApiResponse;
import com.fintrust.customer.dto.DocumentUploadResponse;
import com.fintrust.customer.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/accounts/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "KYC document upload APIs")
public class DocumentController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload KYC document to Cloudinary (PAN/AADHAAR/PASSPORT)")
    public ResponseEntity<ApiResponse<DocumentUploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("docType") String docType
    ) {
        return ResponseEntity.ok(ApiResponse.success("Uploaded", cloudinaryService.uploadDocument(file, docType)));
    }
}

