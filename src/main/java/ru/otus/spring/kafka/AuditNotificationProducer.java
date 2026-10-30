package ru.otus.spring.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class AuditNotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(AuditNotificationProducer.class);

    public static final String TYPE_INCOMING_CALL = "INCOMING_CALL";
    public static final String TYPE_CALL_RESULT = "CALL_RESULT";
    public static final String RESULT_UNKNOWN = "UNKNOWN";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public AuditNotificationProducer(KafkaTemplate<String, String> kafkaTemplate,
                                    ObjectMapper objectMapper,
                                    @Value("${app.kafka.audit.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public void sendIncomingCall(String correlationId, String method, String uri) {
        publish(new AuditMessage(correlationId, TYPE_INCOMING_CALL, method, uri, null, RESULT_UNKNOWN, timestamp()));
    }

    public void sendCallResult(String correlationId, String method, String uri, Integer statusCode, String result) {
        publish(new AuditMessage(correlationId, TYPE_CALL_RESULT, method, uri, statusCode, result, timestamp()));
    }

    private void publish(AuditMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, message.correlationId(), json);
            log.debug("Published audit notification to topic '{}': {}", topic, json);
        } catch (Exception e) {
            log.warn("Failed to publish audit notification to topic '{}': {}", topic, e.getMessage());
        }
    }

    private String timestamp() {
        return OffsetDateTime.now().toString();
    }
}
