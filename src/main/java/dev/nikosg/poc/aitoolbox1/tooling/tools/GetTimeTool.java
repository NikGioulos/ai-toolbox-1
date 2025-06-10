package dev.nikosg.poc.aitoolbox1.tooling.tools;

import dev.nikosg.poc.aitoolbox1.tooling.ToolCallback;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static dev.nikosg.poc.aitoolbox1.tooling.tools.GetTimeTool.GET_TIME_TOOL_BEAN_NAME;

@Component(GET_TIME_TOOL_BEAN_NAME)
public class GetTimeTool implements ToolCallback {
    protected static final String GET_TIME_TOOL_BEAN_NAME = "getTimeTool";

    @Override
    public String getName() {
        return GET_TIME_TOOL_BEAN_NAME;
    }

    @Override
    public String getDescription() {
        return "Get the current server time";
    }

    @Override
    public String getJsonSchema() {
        return """
                {
                    "type": "object",
                    "properties": {},
                    "required": []
                }
                """;
    }

    @Override
    public String execute(String argumentsJson) {
        return "The current time is: " + Instant.now().toString();
    }
}
