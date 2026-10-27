package ru.otus.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import ru.otus.spring.service.QuestionService;
import ru.otus.spring.service.TestRunnerService;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SpringContextTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestRunnerService testRunnerService;

    @Autowired
    private MessageSource messageSource;

    @Test
    void contextLoadsMessageSourceFromSpringBootAutoconfiguration() {
        assertNotNull(messageSource);
        assertNotNull(testRunnerService);
    }

    @Test
    void questionServiceReturnsQuestionsForConfiguredLocale() {
        assertEquals(5, questionService.getAllQuestions().size());
    }

    @Test
    void messageSourceResolvesLocalizedMessages() {
        assertEquals("ЗАЧЁТ", messageSource.getMessage("test.result.passed", null, new Locale("ru")));
        assertEquals("PASSED", messageSource.getMessage("test.result.passed", null, Locale.ENGLISH));
    }
}
