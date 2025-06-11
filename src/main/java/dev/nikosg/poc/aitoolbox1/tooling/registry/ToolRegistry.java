package dev.nikosg.poc.aitoolbox1.tooling.registry;

import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolDef;

import java.util.List;

public interface ToolRegistry {

    ToolRegistryType getType();

    public List<ToolDef> getToolSchemas() throws Exception;

    public String execute(String name, String argsJson) throws Exception;
}
