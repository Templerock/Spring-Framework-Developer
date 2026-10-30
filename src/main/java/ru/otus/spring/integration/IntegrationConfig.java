package ru.otus.spring.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

import ru.otus.spring.domain.Butterfly;
import ru.otus.spring.domain.ButterflyActivity;
import ru.otus.spring.domain.Caterpillar;
import ru.otus.spring.domain.Cocoon;
import ru.otus.spring.service.MetamorphosisService;

@Configuration
public class IntegrationConfig {

    public static final String METAMORPHOSIS_INPUT = "metamorphosisInput";
    public static final String METAMORPHOSIS_OUTPUT = "metamorphosisOutput";
    public static final String METAMORPHOSIS_DISCARD = "metamorphosisDiscarded";

    @Bean
    public MessageChannel metamorphosisInput() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel metamorphosisOutput() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel metamorphosisDiscarded() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow metamorphosisFlow(MetamorphosisService service) {
        return IntegrationFlow.from(METAMORPHOSIS_INPUT)
                .filter(Caterpillar.class, Caterpillar::readyToPupate,
                        f -> f.discardChannel(METAMORPHOSIS_DISCARD))
                .transform(Caterpillar.class, service::pupate)
                .log(LoggingHandler.Level.INFO,
                        m -> "Окуклилась гусеница: " + m.getPayload())
                .route(Cocoon.class, service::activity, r -> r
                        .subFlowMapping(ButterflyActivity.DAY, sf -> sf
                                .transform(Cocoon.class, service::emergeDay)
                                .channel(METAMORPHOSIS_OUTPUT))
                        .subFlowMapping(ButterflyActivity.NIGHT, sf -> sf
                                .transform(Cocoon.class, service::emergeNight)
                                .channel(METAMORPHOSIS_OUTPUT)))
                .get();
    }

    @Bean
    public IntegrationFlow metamorphosisResultFlow(MetamorphosisService service) {
        return IntegrationFlow.from(METAMORPHOSIS_OUTPUT)
                .log(LoggingHandler.Level.INFO,
                        m -> "Вылетела бабочка: " + m.getPayload())
                .transform(Butterfly.class, service::announce)
                .get();
    }

    @Bean
    public IntegrationFlow metamorphosisDiscardFlow() {
        return IntegrationFlow.from(METAMORPHOSIS_DISCARD)
                .log(LoggingHandler.Level.WARN,
                        m -> "Отбракована: гусеница не наелась -> " + m.getPayload())
                .nullChannel();
    }
}
