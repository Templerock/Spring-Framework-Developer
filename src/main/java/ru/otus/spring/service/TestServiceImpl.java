package ru.otus.spring.service;

import java.util.List;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.domain.Student;
import ru.otus.spring.domain.TestResult;

public class TestServiceImpl implements TestService {

    private final QuestionService questionService;
    private final IOService ioService;
    private final int passThreshold;

    public TestServiceImpl(QuestionService questionService, IOService ioService, int passThreshold) {
        this.questionService = questionService;
        this.ioService = ioService;
        this.passThreshold = passThreshold;
    }

    @Override
    public TestResult runTest(Student student) {
        List<Question> questions = questionService.getAllQuestions();
        int correctAnswersCount = 0;
        for (Question question : questions) {
            if (askQuestion(question)) {
                correctAnswersCount++;
            }
        }
        return new TestResult(student, questions.size(), correctAnswersCount, passThreshold);
    }

    private boolean askQuestion(Question question) {
        printQuestion(question);
        ioService.printLine("Your answer:");
        String answer = ioService.readLine();
        return isCorrect(question, answer);
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.getText());
        List<Answer> answers = question.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            ioService.printLine((i + 1) + ") " + answers.get(i).getText());
        }
    }

    private boolean isCorrect(Question question, String answer) {
        return question.getAnswers().get(0).getText().equalsIgnoreCase(answer.trim());
    }
}
