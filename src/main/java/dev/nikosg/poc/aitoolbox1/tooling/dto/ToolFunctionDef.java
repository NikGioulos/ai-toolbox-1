package dev.nikosg.poc.aitoolbox1.tooling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ToolFunctionDef {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("description")
    private final String description;
    @JsonProperty("parameters")
    private final Map<String, Object> parameters;

    public ToolFunctionDef(String name, String description, Map<String, Object> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
