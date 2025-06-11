package dev.nikosg.poc.aitoolbox1.tooling.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nikosg.poc.aitoolbox1.tooling.ToolCallback;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolDef;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolFunctionDef;
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
    public List<ToolDef> getToolSchemas() throws Exception {
        List<ToolDef> tools = new ArrayList<>();
        for (ToolCallback tool : toolRegistry.values()) {

            ToolFunctionDef function = new ToolFunctionDef(
                    tool.getName(),
                    tool.getDescription(),
                    new ObjectMapper().readValue(tool.getJsonSchema(), Map.class)
            );
            ToolDef toolDef = new ToolDef(function);

            tools.add(toolDef);
        }
        return tools;
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