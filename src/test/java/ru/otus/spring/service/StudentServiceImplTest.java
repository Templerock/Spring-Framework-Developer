package ru.otus.spring.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.otus.spring.domain.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentServiceImplTest {

    @Test
    void determineCurrentStudentReadsFirstAndLastName() {
        IOService ioService = new ScriptedIOService(
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                List.of("Ivan", "Petrov"));

        Student student = new StudentServiceImpl(ioService).determineCurrentStudent();

        assertEquals("Ivan", student.getFirstName());
        assertEquals("Petrov", student.getLastName());
    }
}
