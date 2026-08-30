package com.ilyanin.file_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ilyanin.file_service.domain.Permission;

public record FileSharedEvent(
    UUID fileId,
    UUID ownerId,
    UUID sharedWithUserId,
    String originalName,
    Permission permission,
    LocalDateTime sharedAt
) {

}
