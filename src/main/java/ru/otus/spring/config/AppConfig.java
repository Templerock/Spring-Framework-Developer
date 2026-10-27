package ru.otus.spring.config;

import java.io.PrintStream;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.dao.QuestionDaoCsv;
import ru.otus.spring.service.IOService;
import ru.otus.spring.service.IOServiceStreams;
import ru.otus.spring.service.QuestionPrinter;
import ru.otus.spring.service.QuestionPrinterImpl;
import ru.otus.spring.service.QuestionService;
import ru.otus.spring.service.QuestionServiceImpl;
import ru.otus.spring.service.StudentService;
import ru.otus.spring.service.StudentServiceImpl;
import ru.otus.spring.service.TestRunnerService;
import ru.otus.spring.service.TestRunnerServiceImpl;
import ru.otus.spring.service.TestService;
import ru.otus.spring.service.TestServiceImpl;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public QuestionDao questionDao(@Value("${csv.path}") String csvPath) {
        return new QuestionDaoCsv(new ClassPathResource(csvPath));
    }

    @Bean
    public QuestionService questionService(QuestionDao questionDao) {
        return new QuestionServiceImpl(questionDao);
    }

    @Bean
    public IOService ioService() {
        return new IOServiceStreams(new PrintStream(System.out), new Scanner(System.in));
    }

    @Bean
    public QuestionPrinter questionPrinter(IOService ioService) {
        return new QuestionPrinterImpl(ioService);
    }

    @Bean
    public StudentService studentService(IOService ioService) {
        return new StudentServiceImpl(ioService);
    }

    @Bean
    public TestService testService(QuestionService questionService, IOService ioService,
                                  @Value("${test.pass-threshold}") int passThreshold) {
        return new TestServiceImpl(questionService, ioService, passThreshold);
    }

    @Bean
    public TestRunnerService testRunnerService(StudentService studentService, TestService testService,
                                              IOService ioService) {
        return new TestRunnerServiceImpl(studentService, testService, ioService);
    }
}
