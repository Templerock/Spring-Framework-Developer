package ru.otus.spring.service;

import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.Scanner;

@Component
public class IOServiceStreams implements IOService {

    private final PrintStream output = new PrintStream(System.out);
    private final Scanner input = new Scanner(System.in);

    @Override
    public void printLine(String message) {
        output.println(message);
    }

    @Override
    public String readLine() {
        return input.nextLine();
    }
}
