package ru.otus.spring.service;

import org.springframework.stereotype.Component;
import ru.otus.spring.domain.Student;

@Component
public class StudentServiceImpl implements StudentService {

    private final IOService ioService;
    private final MessageService messageService;

    public StudentServiceImpl(IOService ioService, MessageService messageService) {
        this.ioService = ioService;
        this.messageService = messageService;
    }

    @Override
    public Student determineCurrentStudent() {
        ioService.printLine(messageService.getMessage("test.first-name.prompt"));
        String firstName = ioService.readLine();
        ioService.printLine(messageService.getMessage("test.last-name.prompt"));
        String lastName = ioService.readLine();
        return new Student(firstName, lastName);
    }
}
