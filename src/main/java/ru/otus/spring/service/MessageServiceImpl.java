package ru.otus.spring.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageServiceImpl implements MessageService {

    private final MessageSource messageSource;
    private final LocaleService localeService;

    public MessageServiceImpl(MessageSource messageSource, LocaleService localeService) {
        this.messageSource = messageSource;
        this.localeService = localeService;
    }

    @Override
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, localeService.getCurrentLocale());
    }
}
