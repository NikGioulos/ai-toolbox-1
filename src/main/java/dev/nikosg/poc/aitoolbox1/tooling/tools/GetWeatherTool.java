package dev.nikosg.poc.aitoolbox1.tooling.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nikosg.poc.aitoolbox1.tooling.ToolCallback;
import org.springframework.stereotype.Component;

import static dev.nikosg.poc.aitoolbox1.tooling.tools.GetWeatherTool.GET_WEATHER_TOOL_BEAN_NAME;

@Component(value = GET_WEATHER_TOOL_BEAN_NAME)
public class GetWeatherTool implements ToolCallback {
    protected static final String GET_WEATHER_TOOL_BEAN_NAME = "getWeatherTool";

    @Override
    public String getName() {
        return GET_WEATHER_TOOL_BEAN_NAME;
    }

    @Override
    public String getDescription() {
        return "Get the weather for a given city";
    }

    @Override
    public String getJsonSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "location": {
                            "type": "string",
                            "description": "The city name"
                        }
                    },
                    "required": ["location"]
                }
                """;
    }

    @Override
    public String execute(String argumentsJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode args = mapper.readTree(argumentsJson);
        String location = args.get("location").asText();
        return "It's 22°C and sunny in " + location;
    }
}
