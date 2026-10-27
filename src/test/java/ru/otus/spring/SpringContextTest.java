package ru.otus.spring;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.otus.spring.config.AppConfig;
import ru.otus.spring.service.QuestionService;
import ru.otus.spring.service.TestRunnerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpringContextTest {

    @Test
    void annotationContextStartsAndServesQuestions() {
        try (ConfigurableApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            QuestionService questionService = context.getBean(QuestionService.class);
            assertNotNull(questionService);
            assertEquals(5, questionService.getAllQuestions().size());

            TestRunnerService testRunner = context.getBean(TestRunnerService.class);
            assertNotNull(testRunner);

            assertEquals("3", context.getEnvironment().getProperty("test.pass-threshold"));
        }
    }
}
