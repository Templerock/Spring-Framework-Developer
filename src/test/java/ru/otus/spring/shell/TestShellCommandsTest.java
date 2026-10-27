package ru.otus.spring.shell;

import java.util.List;

import org.junit.jupiter.api.Test;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.service.QuestionPrinter;
import ru.otus.spring.service.QuestionService;
import ru.otus.spring.service.TestRunnerService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestShellCommandsTest {

    @Test
    void runDelegatesToTestRunnerService() {
        TestRunnerService testRunnerService = mock(TestRunnerService.class);
        TestShellCommands commands = new TestShellCommands(
                testRunnerService, mock(QuestionService.class), mock(QuestionPrinter.class));

        commands.run();

        verify(testRunnerService).run();
    }

    @Test
    void listPrintsAllQuestions() {
        QuestionService questionService = mock(QuestionService.class);
        QuestionPrinter questionPrinter = mock(QuestionPrinter.class);
        List<Question> questions = List.of(new Question("Q1", List.of(new Answer("A"))));
        when(questionService.getAllQuestions()).thenReturn(questions);
        TestShellCommands commands = new TestShellCommands(
                mock(TestRunnerService.class), questionService, questionPrinter);

        commands.list();

        verify(questionService).getAllQuestions();
        verify(questionPrinter).print(questions);
    }
}
