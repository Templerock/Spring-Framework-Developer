package ru.otus.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ru.otus.spring.config.AppProperties;
import ru.otus.spring.service.TestRunnerService;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class TestingApplication {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(TestingApplication.class, args)) {
            TestRunnerService testRunner = context.getBean(TestRunnerService.class);
            testRunner.run();
        }
    }
}
