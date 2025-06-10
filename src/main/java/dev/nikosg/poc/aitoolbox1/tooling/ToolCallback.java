package dev.nikosg.poc.aitoolbox1.tooling;

public interface ToolCallback {

    String getName(); // tool name as known to OpenAI

    String getDescription(); // what this tool does

    String getJsonSchema(); // parameters schema in OpenAI format

    String execute(String argumentsJson) throws Exception;
}
