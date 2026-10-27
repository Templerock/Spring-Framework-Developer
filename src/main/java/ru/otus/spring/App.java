package ru.otus.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.otus.spring.service.QuestionPrinter;
import ru.otus.spring.service.QuestionService;

public class App {

    private App() {
    }

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml")) {
            QuestionService questionService = context.getBean(QuestionService.class);
            QuestionPrinter questionPrinter = context.getBean(QuestionPrinter.class);
            questionPrinter.print(questionService.getAllQuestions());
        }
    }
}
