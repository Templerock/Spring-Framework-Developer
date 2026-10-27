package ru.otus.spring.dao;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import ru.otus.spring.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionDaoCsvTest {

    @Test
    void findAllReturnsQuestionsWithAnswersFromResource() {
        QuestionDao questionDao = new QuestionDaoCsv(new ClassPathResource("test-questions.csv"));

        List<Question> questions = questionDao.findAll();

        assertEquals(2, questions.size());

        Question first = questions.get(0);
        assertEquals("What is Spring IoC?", first.getText());
        assertEquals(3, first.getAnswers().size());
        assertEquals("Inversion of Control", first.getAnswers().get(0).getText());
        assertEquals("Dependency Injection", first.getAnswers().get(1).getText());
        assertEquals("Bean Factory", first.getAnswers().get(2).getText());

        Question second = questions.get(1);
        assertEquals("What is a Bean?", second.getText());
        assertEquals(1, second.getAnswers().size());
        assertEquals("An object managed by the container", second.getAnswers().get(0).getText());
    }

    @Test
    void findAllReturnsEmptyListForEmptyResource() {
        QuestionDao questionDao = new QuestionDaoCsv(new ClassPathResource("empty-questions.csv"));

        List<Question> questions = questionDao.findAll();

        assertEquals(0, questions.size());
    }
}
