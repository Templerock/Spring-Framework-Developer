package ru.otus.spring.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(AuditNotificationListener.class);

    @KafkaListener(topics = "${app.kafka.audit.topic}", groupId = "notification-audit-consumer")
    public void onNotification(String message) {
        log.info("AUDIT :: received notification: {}", message);
    }
}
