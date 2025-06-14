package dev.nikosg.poc.aitoolbox1.tooling.registry;

import com.openai.models.chat.completions.ChatCompletionTool;

import java.util.List;

public interface ToolRegistry {

    ToolRegistryType getType();

    public List<ChatCompletionTool> getToolSchemas() throws Exception;

    public String execute(String name, String argsJson) throws Exception;
}
