package ru.otus.spring.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import ru.otus.spring.domain.Caterpillar;

@MessagingGateway(name = "metamorphosisGateway", defaultRequestChannel = IntegrationConfig.METAMORPHOSIS_INPUT)
public interface MetamorphosisGateway {

    @Gateway
    String transform(Caterpillar caterpillar);
}
