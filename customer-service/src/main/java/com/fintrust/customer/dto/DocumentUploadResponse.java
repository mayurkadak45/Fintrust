package com.fintrust.customer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentUploadResponse {
    private String url;
    private String publicId;
    private String resourceType;
    private String format;
    private Long bytes;
}

