package ru.otus.spring.service;

import org.springframework.stereotype.Component;
import ru.otus.spring.config.AppProperties;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.domain.Student;
import ru.otus.spring.domain.TestResult;

import java.util.List;

@Component
public class TestServiceImpl implements TestService {

    private final QuestionService questionService;
    private final IOService ioService;
    private final MessageService messageService;
    private final AppProperties appProperties;

    public TestServiceImpl(QuestionService questionService, IOService ioService,
                           MessageService messageService, AppProperties appProperties) {
        this.questionService = questionService;
        this.ioService = ioService;
        this.messageService = messageService;
        this.appProperties = appProperties;
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
        return new TestResult(student, questions.size(), correctAnswersCount, appProperties.getTest().getPassThreshold());
    }

    private boolean askQuestion(Question question) {
        printQuestion(question);
        ioService.printLine(messageService.getMessage("test.answer.prompt"));
        String answer = ioService.readLine();
        return isCorrect(question, answer);
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.getText());
        List<Answer> answers = question.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            ioService.printLine((i + 1) + ". " + answers.get(i).getText());
        }
    }

    private boolean isCorrect(Question question, String answer) {
        return question.getAnswers().get(0).getText().equalsIgnoreCase(answer.trim());
    }
}
