package ru.otus.spring.domain;

import java.util.Collections;
import java.util.List;

public class Question {

    private final String text;
    private final List<Answer> answers;

    public Question(String text, List<Answer> answers) {
        this.text = text;
        this.answers = Collections.unmodifiableList(answers);
    }

    public String getText() {
        return text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
