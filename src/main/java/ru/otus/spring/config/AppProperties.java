package ru.otus.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties("application")
public class AppProperties {

    private Locale locale = Locale.ENGLISH;

    private final Test test = new Test();

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Test getTest() {
        return test;
    }

    public static class Test {

        private String csvPath = "questions_%s.csv";

        private int passThreshold = 3;

        public String getCsvPath() {
            return csvPath;
        }

        public void setCsvPath(String csvPath) {
            this.csvPath = csvPath;
        }

        public int getPassThreshold() {
            return passThreshold;
        }

        public void setPassThreshold(int passThreshold) {
            this.passThreshold = passThreshold;
        }
    }
}
