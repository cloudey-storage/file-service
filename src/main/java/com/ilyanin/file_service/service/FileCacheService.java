package com.ilyanin.file_service.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ilyanin.file_service.api.dto.FileMetadataResponse;

@Service
public class FileCacheService {

    private static final String CACHE_KEY_PREFIX = "user:%s:files";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;

    public FileCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @SuppressWarnings("unchecked")
    public List<FileMetadataResponse> get(UUID ownerId) {
        String key = buildKey(ownerId);
        return (List<FileMetadataResponse>) redisTemplate.opsForValue().get(key);
    }

    public void put(UUID ownerId, List<FileMetadataResponse> files) {
        String key = buildKey(ownerId);
        redisTemplate.opsForValue().set(key, files, TTL);
    }

    public void evict(UUID ownerId) {
        String key = buildKey(ownerId);
        redisTemplate.delete(key);
    }

    private String buildKey(UUID ownerId) {
        return String.format(CACHE_KEY_PREFIX, ownerId);
    }
}
