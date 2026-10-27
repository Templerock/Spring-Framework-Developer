package ru.otus.spring.service;

import java.io.PrintStream;
import java.util.List;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

public class QuestionPrinterImpl implements QuestionPrinter {

    private final PrintStream output;

    public QuestionPrinterImpl() {
        this(System.out);
    }

    QuestionPrinterImpl(PrintStream output) {
        this.output = output;
    }

    @Override
    public void print(List<Question> questions) {
        output.println("Test questions:");
        for (int i = 0; i < questions.size(); i++) {
            printQuestion(i + 1, questions.get(i));
        }
    }

    private void printQuestion(int number, Question question) {
        output.println(number + ". " + question.getText());
        for (Answer answer : question.getAnswers()) {
            output.println("   - " + answer.getText());
        }
    }
}
