package ru.otus.spring.domain;

public class TestResult {

    private final Student student;
    private final int totalQuestionsCount;
    private final int correctAnswersCount;
    private final int passThreshold;

    public TestResult(Student student, int totalQuestionsCount, int correctAnswersCount, int passThreshold) {
        this.student = student;
        this.totalQuestionsCount = totalQuestionsCount;
        this.correctAnswersCount = correctAnswersCount;
        this.passThreshold = passThreshold;
    }

    public Student getStudent() {
        return student;
    }

    public int getTotalQuestionsCount() {
        return totalQuestionsCount;
    }

    public int getCorrectAnswersCount() {
        return correctAnswersCount;
    }

    public boolean isPassed() {
        return correctAnswersCount >= passThreshold;
    }
}
