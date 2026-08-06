package com.ilyanin.file_service.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ilyanin.file_service.persistence.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, UUID>{

    List<FileEntity> findAllByOwnerIdAndIsDeletedFalse(UUID ownerId);

    List<FileEntity> findAllByOwnerIdAndIsDeletedTrue(UUID ownerId);

    Optional<FileEntity> findByIdAndIsDeletedFalse(UUID id);
    
    boolean existsByMinioKey(String minioKey);
}
