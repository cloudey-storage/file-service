package com.ilyanin.file_service.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.ilyanin.file_service.domain.Permission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Check migrations for info about indexes
@Table(name = "file_shares")
@Entity
public class FileShareEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private FileEntity file;

    @Column(name = "shared_with_user_id", nullable = false)
    private UUID sharedWithUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false)
    private Permission permission = Permission.READ;

    @CreationTimestamp
    @Column(name = "shared_at", nullable = false)
    private LocalDateTime sharedAt;

    public FileShareEntity(FileEntity file, UUID sharedWithUserId) {
        this.file = file;
        this.sharedWithUserId = sharedWithUserId;
    }  

    public FileShareEntity() {}

    public UUID getId() {
        return id;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public UUID getSharedWithUserId() {
        return sharedWithUserId;
    }

    public void setSharedWithUserId(UUID sharedWithUserId) {
        this.sharedWithUserId = sharedWithUserId;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }
}
