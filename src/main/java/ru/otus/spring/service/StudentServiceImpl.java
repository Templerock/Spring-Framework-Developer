package ru.otus.spring.service;

import ru.otus.spring.domain.Student;

public class StudentServiceImpl implements StudentService {

    private final IOService ioService;

    public StudentServiceImpl(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public Student determineCurrentStudent() {
        ioService.printLine("Please enter your first name:");
        String firstName = ioService.readLine();
        ioService.printLine("Please enter your last name:");
        String lastName = ioService.readLine();
        return new Student(firstName, lastName);
    }
}
