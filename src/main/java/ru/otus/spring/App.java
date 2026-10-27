package ru.otus.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.otus.spring.config.AppConfig;
import ru.otus.spring.service.TestRunnerService;

public class App {

    private App() {
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            TestRunnerService testRunner = context.getBean(TestRunnerService.class);
            testRunner.run();
        }
    }
}
