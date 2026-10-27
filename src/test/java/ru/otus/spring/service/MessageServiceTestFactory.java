package ru.otus.spring.service;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public final class MessageServiceTestFactory {

    private MessageServiceTestFactory() {
    }

    public static MessageService create(Locale locale) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return new MessageServiceImpl(messageSource, () -> locale);
    }
}
