package com.ilyanin.file_service.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

@Component
public class MinioBucketInitializer implements ApplicationRunner {

    private final MinioClient client;
    private final MinioProperties properties;

    public MinioBucketInitializer(MinioClient client, MinioProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean exists = client.bucketExists(
            BucketExistsArgs.builder().bucket(properties.bucketName()).build()
        );
        if (!exists) {
            client.makeBucket(
                MakeBucketArgs.builder().bucket(properties.bucketName()).build()
            );
        }
    }
}
