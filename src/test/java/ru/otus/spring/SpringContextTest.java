package ru.otus.spring;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.otus.spring.service.QuestionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpringContextTest {

    @Test
    void xmlContextStartsAndServesQuestions() {
        try (ConfigurableApplicationContext context =
                     new ClassPathXmlApplicationContext("spring-context.xml")) {
            QuestionService questionService = context.getBean(QuestionService.class);
            assertNotNull(questionService);
            assertEquals(5, questionService.getAllQuestions().size());
        }
    }
}
