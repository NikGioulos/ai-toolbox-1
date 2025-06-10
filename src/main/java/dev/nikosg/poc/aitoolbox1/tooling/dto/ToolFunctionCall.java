package dev.nikosg.poc.aitoolbox1.tooling.dto;

public class ToolFunctionCall {
    private final String name;
    private final String arguments;

    public ToolFunctionCall(String name, String arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String getArguments() {
        return arguments;
    }
}
