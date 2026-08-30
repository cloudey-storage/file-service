package com.ilyanin.file_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileDeletedEvent(
    UUID fileId,
    UUID ownerId,
    String originalName,
    LocalDateTime deletedAt
) {

}
