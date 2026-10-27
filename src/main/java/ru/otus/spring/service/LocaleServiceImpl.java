package ru.otus.spring.service;

import org.springframework.stereotype.Component;
import ru.otus.spring.config.AppProperties;

import java.util.Locale;

@Component
public class LocaleServiceImpl implements LocaleService {

    private final AppProperties appProperties;

    public LocaleServiceImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public Locale getCurrentLocale() {
        return appProperties.getLocale();
    }
}
