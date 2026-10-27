package ru.otus.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.dao.QuestionDaoCsv;
import ru.otus.spring.service.LocaleService;

@Configuration
public class DaoConfig {

    @Bean
    public QuestionDao questionDao(AppProperties appProperties, LocaleService localeService) {
        String csvPath = String.format(appProperties.getTest().getCsvPath(), localeService.getCurrentLocale());
        return new QuestionDaoCsv(new ClassPathResource(csvPath));
    }
}
