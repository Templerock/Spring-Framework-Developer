package ru.otus.spring.service;

import java.io.PrintStream;
import java.util.List;

public class ScriptedIOService implements IOService {

    private final PrintStream output;
    private final List<String> script;
    private int position;

    public ScriptedIOService(PrintStream output, List<String> script) {
        this.output = output;
        this.script = script;
    }

    @Override
    public void printLine(String message) {
        output.println(message);
    }

    @Override
    public String readLine() {
        return script.get(position++);
    }
}
