package ru.otus.spring.kafka;

public record AuditMessage(
        String correlationId,
        String type,
        String method,
        String uri,
        Integer statusCode,
        String result,
        String timestamp) {
}
