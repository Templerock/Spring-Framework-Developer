package ru.otus.spring.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.otus.spring.domain.Caterpillar;
import ru.otus.spring.integration.MetamorphosisGateway;

@RestController
@RequestMapping("/api/metamorphosis")
public class MetamorphosisController {

    private final MetamorphosisGateway metamorphosisGateway;

    public MetamorphosisController(MetamorphosisGateway metamorphosisGateway) {
        this.metamorphosisGateway = metamorphosisGateway;
    }

    @PostMapping
    public String transform(@RequestBody Caterpillar caterpillar) {
        return metamorphosisGateway.transform(caterpillar);
    }
}
