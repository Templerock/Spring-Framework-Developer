package ru.otus.spring.service;

public interface MessageService {

    String getMessage(String code, Object... args);
}
