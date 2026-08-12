package com.ilyanin.file_service.service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.ilyanin.file_service.config.MinioProperties;
import com.ilyanin.file_service.exception.FileStorageException;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;

@Service
public class FileStorageService {

    private final MinioClient client;
    private final MinioProperties properties;

    public FileStorageService(MinioClient client, MinioProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public void upload(String key, InputStream data, long size, String contentType) {
        try {
            client.putObject(
                PutObjectArgs.builder()
                    .bucket(properties.bucketName())
                    .object(key)
                    .stream(data, size, -1)
                    .contentType(contentType)
                    .build()
            );
        } catch (Exception e) {
            throw new FileStorageException("Failed to upload file to storage: " + key, e);
        }
    }

    public String generatePresignedDownloadUrl(String key) {
        try {
            return client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(properties.bucketName())
                    .object(key)
                    .expiry(15, TimeUnit.MINUTES)
                    .build()
            );
        } catch (Exception e) {
            throw new FileStorageException("Failed to generate download URL: " + key, e);
        }
    }

    public void delete(String key) {
        try {
            client.removeObject(
                RemoveObjectArgs.builder()
                .bucket(properties.bucketName())
                .object(key)
                .build()
            );
        } catch (Exception e) {
            throw new FileStorageException("Failed to delete file: " + key, e);
        }
    }
}
