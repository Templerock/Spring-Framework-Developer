package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

class QuestionServiceImplTest {

    @Test
    void getAllQuestionsReturnsQuestionsFromDao() {
        List<Question> expected = List.of(
                new Question("What is Spring?", List.of(new Answer("IoC"))));
        QuestionDao questionDao = () -> expected;
        QuestionServiceImpl questionService = new QuestionServiceImpl(questionDao);

        List<Question> actual = questionService.getAllQuestions();

        assertSame(expected, actual);
    }
}
