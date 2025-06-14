package dev.nikosg.poc.aitoolbox1.tooling.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.ChatCompletionTool;
import dev.nikosg.poc.aitoolbox1.tooling.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClassLevelToolRegistry implements ToolRegistry {

    private final Map<String, ToolCallback> toolRegistry;

    public ClassLevelToolRegistry(Map<String, ToolCallback> toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    @Override
    public ToolRegistryType getType() {
        return ToolRegistryType.CLASS;
    }

    @Override
    public List<ChatCompletionTool> getToolSchemas() throws Exception {
        List<ChatCompletionTool> tools = new ArrayList<>();
        for (ToolCallback tool : toolRegistry.values()) {
            Map<String, Object> parameters = new ObjectMapper().readValue(tool.getJsonSchema(), Map.class);

            FunctionDefinition function = FunctionDefinition.builder()
                    .name(tool.getName())
                    .description(tool.getDescription())
                    .parameters(toFunctionParameters(parameters))
                    .build();

            ChatCompletionTool toolDef = ChatCompletionTool.builder()
                    .type(JsonValue.from("function"))
                    .function(function)
                    .build();

            tools.add(toolDef);
        }
        return tools;
    }

    private FunctionParameters toFunctionParameters(Map<String, Object> parameters) {
        FunctionParameters.Builder builder = FunctionParameters.builder();
        parameters.forEach((key, value) -> builder.putAdditionalProperty(key, JsonValue.from(value)));
        return builder.build();
    }

    @Override
    public String execute(String name, String argsJson) throws Exception {
        ToolCallback tool = toolRegistry.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("No such tool: " + name);
        }
        return tool.execute(argsJson);
    }
}