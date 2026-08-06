package com.ilyanin.file_service.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ilyanin.file_service.persistence.entity.FileEntity;
import com.ilyanin.file_service.persistence.entity.FileShareEntity;

public interface FileShareRepository extends JpaRepository<FileShareEntity, UUID>{

    List<FileShareEntity> findAllByFileId(UUID fileId);

    @Query("""
        SELECT fs.file FROM FileShareEntity fs
        WHERE fs.sharedWithUserId = :userId
        AND fs.file.isDeleted = false
            """)
    List<FileEntity> findAllSharedWithUser(@Param("userId") UUID userId);

    Optional<FileShareEntity> findByFileIdAndSharedWithUserId(UUID fileId, UUID sharedWithUserId);

    void deleteByFileIdAndSharedWithUserId(UUID fileId, UUID sharedWithUserId);
}
