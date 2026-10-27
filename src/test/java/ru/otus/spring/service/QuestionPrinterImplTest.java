package ru.otus.spring.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionPrinterImplTest {

    @Test
    void printWritesQuestionTextAndAnswersToOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOService ioService = new ScriptedIOService(
                new PrintStream(output, true, StandardCharsets.UTF_8), List.of());
        QuestionPrinter questionPrinter = new QuestionPrinterImpl(ioService);

        questionPrinter.print(List.of(
                new Question("What is Spring?",
                        List.of(new Answer("IoC"), new Answer("AOP")))));

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Test questions:"));
        assertTrue(text.contains("What is Spring?"));
        assertTrue(text.contains("IoC"));
        assertTrue(text.contains("AOP"));
    }

    @Test
    void printWritesAllPrintedQuestions() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOService ioService = new ScriptedIOService(
                new PrintStream(output, true, StandardCharsets.UTF_8), List.of());
        QuestionPrinter questionPrinter = new QuestionPrinterImpl(ioService);

        questionPrinter.print(List.of(
                new Question("First question?", List.of(new Answer("First answer"))),
                new Question("Second question?", List.of(new Answer("Second answer")))));

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("First question?"));
        assertTrue(text.contains("Second question?"));
    }
}
