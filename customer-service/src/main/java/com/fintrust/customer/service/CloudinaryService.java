package com.fintrust.customer.service;

import org.springframework.web.multipart.MultipartFile;

import com.fintrust.customer.dto.DocumentUploadResponse;

public interface CloudinaryService {

	DocumentUploadResponse uploadDocument(MultipartFile file, String docType);

}