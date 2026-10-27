package ru.otus.spring.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ru.otus.spring.service.QuestionPrinter;
import ru.otus.spring.service.QuestionService;
import ru.otus.spring.service.TestRunnerService;

@ShellComponent
public class TestShellCommands {

    private final TestRunnerService testRunnerService;
    private final QuestionService questionService;
    private final QuestionPrinter questionPrinter;

    public TestShellCommands(TestRunnerService testRunnerService, QuestionService questionService,
                             QuestionPrinter questionPrinter) {
        this.testRunnerService = testRunnerService;
        this.questionService = questionService;
        this.questionPrinter = questionPrinter;
    }

    @ShellMethod(value = "Run the whole test: ask for the student and all questions", key = "run")
    public void run() {
        testRunnerService.run();
    }

    @ShellMethod(value = "Show all questions", key = "list")
    public void list() {
        questionPrinter.print(questionService.getAllQuestions());
    }
}
