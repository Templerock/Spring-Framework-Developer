package ru.otus.spring.service;

import java.io.PrintStream;
import java.util.Scanner;

public class IOServiceStreams implements IOService {

    private final PrintStream output;
    private final Scanner input;

    public IOServiceStreams(PrintStream output, Scanner input) {
        this.output = output;
        this.input = input;
    }

    @Override
    public void printLine(String message) {
        output.println(message);
    }

    @Override
    public String readLine() {
        return input.nextLine();
    }
}
