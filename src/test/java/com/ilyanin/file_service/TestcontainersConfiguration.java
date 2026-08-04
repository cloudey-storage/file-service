package com.ilyanin.file_service;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
// import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Disabled
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    // // 1. Ручной запуск и маппинг для Kafka (т.к. @ServiceConnection его не распознал)
    // static final KafkaContainer kafkaContainer =
    //         new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    // static {
    //     kafkaContainer.start();
    // }

    // @DynamicPropertySource
    // static void kafkaProperties(DynamicPropertyRegistry registry) {
    //     registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    // }

    // 2. Автоматический маппинг для PostgreSQL
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));
    }

    // 3. Автоматический маппинг для Redis
    @SuppressWarnings("resource")
    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7")).withExposedPorts(6379);
    }
}