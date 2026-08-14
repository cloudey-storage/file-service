package com.ilyanin.file_service.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ilyanin.file_service.api.dto.FileMetadataResponse;
import com.ilyanin.file_service.domain.AggregateType;
import com.ilyanin.file_service.domain.EventType;
import com.ilyanin.file_service.exception.FileStorageException;
import com.ilyanin.file_service.persistence.entity.FileEntity;
import com.ilyanin.file_service.persistence.entity.OutboxEventEntity;
import com.ilyanin.file_service.persistence.repository.FileRepository;
import com.ilyanin.file_service.persistence.repository.OutboxEventRepository;

import jakarta.transaction.Transactional;

@Service
public class FileServiceImpl {

    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final FileCacheService fileCacheService;
    private final Mapper mapper;
    
    public FileServiceImpl(
        FileStorageService fileStorageService, 
        FileRepository fileRepository,
        OutboxEventRepository outboxEventRepository,
        FileCacheService fileCacheService,
        Mapper mapper
    ) {
        this.fileStorageService = fileStorageService;
        this.fileRepository = fileRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.fileCacheService = fileCacheService;
        this.mapper = mapper;
    }


    @Transactional
    public FileMetadataResponse uploadFile(UUID ownerId, MultipartFile file) {
        String minioKey = ownerId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            fileStorageService.upload(minioKey, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new FileStorageException("Failed to read uploaded file: " + file.getOriginalFilename(), e);
        }
        FileEntity fileEntity = new FileEntity(
            ownerId,
            file.getOriginalFilename(),
            minioKey,
            file.getContentType(),
            file.getSize(),
            false
        );
        FileEntity savedFile = fileRepository.save(fileEntity);

        OutboxEventEntity outboxEvent = new OutboxEventEntity(
            AggregateType.FILE,
            savedFile.getId(),
            EventType.FILE_UPLOADED,
            "payload"
        );
        outboxEventRepository.save(outboxEvent);

        fileCacheService.evict(ownerId);

        return mapper.toResponse(savedFile);
    }
}
