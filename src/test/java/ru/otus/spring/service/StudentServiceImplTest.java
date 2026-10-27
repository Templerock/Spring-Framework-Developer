package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import ru.otus.spring.domain.Student;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentServiceImplTest {

    @Test
    void determineCurrentStudentReadsFirstAndLastName() {
        IOService ioService = new ScriptedIOService(
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                List.of("Ivan", "Petrov"));
        MessageService messageService = MessageServiceTestFactory.create(Locale.ENGLISH);

        Student student = new StudentServiceImpl(ioService, messageService).determineCurrentStudent();

        assertEquals("Ivan", student.getFirstName());
        assertEquals("Petrov", student.getLastName());
    }
}
