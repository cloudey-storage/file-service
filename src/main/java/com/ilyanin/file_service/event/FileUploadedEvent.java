package com.ilyanin.file_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileUploadedEvent(
    UUID fileId,
    UUID ownerId,
    String originalName,
    Long sizeBytes,
    LocalDateTime uploadedAt
) {

}
