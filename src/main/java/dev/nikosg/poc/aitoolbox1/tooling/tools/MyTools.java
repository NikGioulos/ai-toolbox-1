package dev.nikosg.poc.aitoolbox1.tooling.tools;

import dev.nikosg.poc.aitoolbox1.tooling.annotations.Tool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class MyTools {

    @Tool(name = "get_weather", description = "Returns weather for a given city")
    public String getWeather(@ToolParam("location") String location) {
        return "It's sunny in " + location;
    }

    @Tool(name = "get_time", description = "Returns current server time")
    public String getTime() {
        return LocalTime.now().toString();
    }
}
