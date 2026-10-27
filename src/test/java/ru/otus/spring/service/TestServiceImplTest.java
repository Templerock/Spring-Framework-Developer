package ru.otus.spring.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.domain.Student;
import ru.otus.spring.domain.TestResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestServiceImplTest {

    @Test
    void runTestCountsOnlyCorrectAnswers() {
        List<Question> questions = List.of(
                new Question("What is Spring IoC?",
                        List.of(new Answer("Inversion of Control"), new Answer("Dependency Injection"))),
                new Question("What is AOP?",
                        List.of(new Answer("Aspect Oriented Programming"))));
        IOService ioService = new ScriptedIOService(
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                List.of("inversion of control", "wrong answer"));
        TestService testService = new TestServiceImpl(() -> questions, ioService, 2);

        TestResult result = testService.runTest(new Student("Ivan", "Petrov"));

        assertEquals(1, result.getCorrectAnswersCount());
        assertEquals(2, result.getTotalQuestionsCount());
        assertFalse(result.isPassed());
    }

    @Test
    void runTestMarksPassedWhenThresholdIsReached() {
        List<Question> questions = List.of(
                new Question("Q1", List.of(new Answer("A"))),
                new Question("Q2", List.of(new Answer("B"))));
        IOService ioService = new ScriptedIOService(
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                List.of("A", "B"));
        TestService testService = new TestServiceImpl(() -> questions, ioService, 2);

        TestResult result = testService.runTest(new Student("Ivan", "Petrov"));

        assertEquals(2, result.getCorrectAnswersCount());
        assertTrue(result.isPassed());
    }
}
