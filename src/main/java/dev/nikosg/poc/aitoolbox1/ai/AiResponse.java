package dev.nikosg.poc.aitoolbox1.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class AiResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("model")
    private String model;
    @JsonProperty("choices")
    private List<Choice> choices;
    @JsonProperty("usage")
    private Map<String, Object> usage;

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public Map<String, Object> getUsage() {
        return usage;
    }
}
