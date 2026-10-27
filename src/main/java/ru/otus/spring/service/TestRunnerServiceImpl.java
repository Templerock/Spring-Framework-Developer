package ru.otus.spring.service;

import ru.otus.spring.domain.Student;
import ru.otus.spring.domain.TestResult;

public class TestRunnerServiceImpl implements TestRunnerService {

    private final StudentService studentService;
    private final TestService testService;
    private final IOService ioService;

    public TestRunnerServiceImpl(StudentService studentService, TestService testService, IOService ioService) {
        this.studentService = studentService;
        this.testService = testService;
        this.ioService = ioService;
    }

    @Override
    public void run() {
        Student student = studentService.determineCurrentStudent();
        TestResult result = testService.runTest(student);
        printResult(result);
    }

    private void printResult(TestResult result) {
        Student student = result.getStudent();
        ioService.printLine("Student: " + student.getFirstName() + " " + student.getLastName());
        ioService.printLine("Correct answers: " + result.getCorrectAnswersCount()
                + " out of " + result.getTotalQuestionsCount());
        ioService.printLine("Result: " + (result.isPassed() ? "PASSED" : "FAILED"));
    }
}
