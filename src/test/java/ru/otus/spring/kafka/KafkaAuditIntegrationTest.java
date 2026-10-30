package ru.otus.spring.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.listener.auto-startup=true"
})
@EmbeddedKafka(partitions = 1, topics = "notification-command")
class KafkaAuditIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuditNotificationProducer producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void incomingCallAndResultArePublishedToNotificationTopic() throws Exception {
        Map<String, Object> props = KafkaTestUtils.consumerProps("test-audit-group", "false", embeddedKafka);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        try (Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<String, String>(props).createConsumer()) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "notification-command");

            producer.sendIncomingCall("corr-1", "GET", "/api/books/1");
            producer.sendCallResult("corr-1", "GET", "/api/books/1", 200, "SUCCESS");

            List<ConsumerRecord<String, String>> messages = new ArrayList<>();
            int attempts = 20;
            while (messages.size() < 2 && attempts-- > 0) {
                ConsumerRecords<String, String> polled = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));
                polled.records("notification-command").forEach(messages::add);
            }
            assertThat(messages).hasSize(2);

            JsonNode incomingMessage = objectMapper.readTree(messages.get(0).value());
            JsonNode resultMessage = objectMapper.readTree(messages.get(1).value());

            assertThat(messages.get(0).key()).isEqualTo("corr-1");
            assertThat(incomingMessage.path("type").asText()).isEqualTo("INCOMING_CALL");
            assertThat(incomingMessage.path("correlationId").asText()).isEqualTo("corr-1");
            assertThat(incomingMessage.path("result").asText()).isEqualTo("UNKNOWN");

            assertThat(resultMessage.path("type").asText()).isEqualTo("CALL_RESULT");
            assertThat(resultMessage.path("statusCode").asInt()).isEqualTo(200);
            assertThat(resultMessage.path("result").asText()).isEqualTo("SUCCESS");
        }
    }
}
