package ru.otus.spring.service;

import java.util.List;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

public class QuestionPrinterImpl implements QuestionPrinter {

    private final IOService ioService;

    public QuestionPrinterImpl(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void print(List<Question> questions) {
        ioService.printLine("Test questions:");
        for (int i = 0; i < questions.size(); i++) {
            printQuestion(i + 1, questions.get(i));
        }
    }

    private void printQuestion(int number, Question question) {
        ioService.printLine(number + ". " + question.getText());
        for (Answer answer : question.getAnswers()) {
            ioService.printLine("   - " + answer.getText());
        }
    }
}
