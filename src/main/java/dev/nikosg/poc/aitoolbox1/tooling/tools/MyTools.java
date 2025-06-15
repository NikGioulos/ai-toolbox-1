package dev.nikosg.poc.aitoolbox1.tooling.tools;

import dev.nikosg.poc.aitoolbox1.service.MyService;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.Tool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class MyTools {

    private final MyService myService;

    public MyTools(MyService myService) {
        this.myService = myService;
    }

    @Tool(name = "get_weather", description = "Returns weather for a given city/country")
    public String getWeather(@ToolParam("city or country") String location) {
        return myService.getWeather(location);
    }

    @Tool(name = "get_time", description = "Returns current server time")
    public String getTime() {
        return myService.getTime();
    }
}
