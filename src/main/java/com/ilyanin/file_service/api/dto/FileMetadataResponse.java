package com.ilyanin.file_service.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileMetadataResponse(
    UUID id,
    String originalName,
    String contentType,
    Long sizeBytes,
    LocalDateTime uploadedAt
) {

}
