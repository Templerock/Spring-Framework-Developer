package ru.otus.spring.service;

import org.springframework.stereotype.Component;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import java.util.List;

@Component
public class QuestionPrinterImpl implements QuestionPrinter {

    private final IOService ioService;
    private final MessageService messageService;

    public QuestionPrinterImpl(IOService ioService, MessageService messageService) {
        this.ioService = ioService;
        this.messageService = messageService;
    }

    @Override
    public void print(List<Question> questions) {
        ioService.printLine(messageService.getMessage("test.questions.header"));
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
