package ru.otus.spring.service;

import org.springframework.stereotype.Component;
import ru.otus.spring.domain.Student;
import ru.otus.spring.domain.TestResult;

@Component
public class TestRunnerServiceImpl implements TestRunnerService {

    private final StudentService studentService;
    private final TestService testService;
    private final IOService ioService;
    private final MessageService messageService;

    public TestRunnerServiceImpl(StudentService studentService, TestService testService,
                                 IOService ioService, MessageService messageService) {
        this.studentService = studentService;
        this.testService = testService;
        this.ioService = ioService;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        Student student = studentService.determineCurrentStudent();
        TestResult result = testService.runTest(student);
        printResult(result);
    }

    private void printResult(TestResult result) {
        Student student = result.getStudent();
        ioService.printLine(messageService.getMessage(
                "test.result.student", student.getFirstName(), student.getLastName()));
        ioService.printLine(messageService.getMessage(
                "test.result.correct", result.getCorrectAnswersCount(), result.getTotalQuestionsCount()));
        ioService.printLine(messageService.getMessage(result.isPassed() ? "test.result.passed" : "test.result.failed"));
    }
}
