package ru.otus.spring.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

public class QuestionDaoCsv implements QuestionDao {

    private static final String COLUMN_SEPARATOR = ";";

    private final Resource questionResource;

    public QuestionDaoCsv(Resource questionResource) {
        this.questionResource = questionResource;
    }

    @Override
    public List<Question> findAll() {
        try (InputStream inputStream = questionResource.getInputStream()) {
            return readQuestions(inputStream);
        } catch (IOException e) {
            throw new QuestionReadException(
                    "Failed to read questions from resource: " + questionResource.getDescription(), e);
        }
    }

    private List<Question> readQuestions(InputStream inputStream) throws IOException {
        List<Question> questions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            addQuestionFromLine(questions, line);
        }
        return questions;
    }

    private void addQuestionFromLine(List<Question> questions, String line) {
        if (line.isBlank()) {
            return;
        }
        List<String> columns = Arrays.asList(line.split(COLUMN_SEPARATOR));
        List<Answer> answers = new ArrayList<>();
        for (String column : columns.subList(1, columns.size())) {
            answers.add(new Answer(column));
        }
        questions.add(new Question(columns.get(0), answers));
    }
}
