package com.ilyanin.file_service.service;

import com.ilyanin.file_service.api.dto.FileMetadataResponse;
import com.ilyanin.file_service.persistence.entity.FileEntity;

public class Mapper {
    public FileMetadataResponse toResponse(FileEntity fileEntity) {
        return new FileMetadataResponse(
            fileEntity.getId(),
            fileEntity.getOriginalName(),
            fileEntity.getContentType(),
            fileEntity.getSizeBytes(),
            fileEntity.getUploadedAt()
        );
    }
}
