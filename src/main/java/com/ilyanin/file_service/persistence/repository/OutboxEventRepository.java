package com.ilyanin.file_service.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ilyanin.file_service.persistence.entity.OutboxEventEntity;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID>{

    List<OutboxEventEntity> findTop100ByProcessedFalseOrderByCreatedAtAsc();
}
