package com.fintrust.customer.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fintrust.common.exception.BadRequestException;
import com.fintrust.customer.dto.DocumentUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:fintrust}")
    private String folder;

    public DocumentUploadResponse uploadDocument(MultipartFile file, String docType) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        if (docType == null || docType.isBlank()) {
            throw new BadRequestException("docType is required");
        }

        try {
            Map<?, ?> res = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder + "/kyc/" + docType.toLowerCase(),
                    "resource_type", "auto"
            ));

            return DocumentUploadResponse.builder()
                    .url(String.valueOf(res.get("secure_url")))
                    .publicId(String.valueOf(res.get("public_id")))
                    .resourceType(String.valueOf(res.get("resource_type")))
                    .format(res.get("format") != null ? String.valueOf(res.get("format")) : null)
                    .bytes(res.get("bytes") instanceof Number ? ((Number) res.get("bytes")).longValue() : null)
                    .build();
        } catch (Exception e) {
            throw new BadRequestException("Upload failed: " + e.getMessage());
        }
    }
}

