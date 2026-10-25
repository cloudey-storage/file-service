package com.ilyanin.file_service.service;

import org.springframework.stereotype.Component;

import com.ilyanin.file_service.api.dto.FileMetadataResponse;
import com.ilyanin.file_service.persistence.entity.FileEntity;

@Component
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
