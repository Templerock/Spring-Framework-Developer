package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionPrinterImplTest {

    @Test
    void printWritesQuestionTextAndAnswersToOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOService ioService = new ScriptedIOService(
                new PrintStream(output, true, StandardCharsets.UTF_8), List.of());
        MessageService messageService = MessageServiceTestFactory.create(Locale.ENGLISH);
        QuestionPrinter questionPrinter = new QuestionPrinterImpl(ioService, messageService);

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
    void printLocalizesHeaderViaMessageService() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOService ioService = new ScriptedIOService(
                new PrintStream(output, true, StandardCharsets.UTF_8), List.of());
        MessageService messageService = MessageServiceTestFactory.create(new Locale("ru"));
        QuestionPrinter questionPrinter = new QuestionPrinterImpl(ioService, messageService);

        questionPrinter.print(List.of(
                new Question("Вопрос?", List.of(new Answer("Ответ")))));

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Вопросы теста:"));
    }
}
