package ru.otus.spring.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public record Caterpillar(String name, String species, int leavesEaten) {

    private static final int MIN_LEAVES = 10;

    @JsonCreator
    public Caterpillar {
    }

    public boolean readyToPupate() {
        return leavesEaten >= MIN_LEAVES;
    }
}
