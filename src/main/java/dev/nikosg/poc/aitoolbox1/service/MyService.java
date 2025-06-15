package dev.nikosg.poc.aitoolbox1.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class MyService {
    public String getWeather(String location) {
        return "It's sunny in " + location;
    }

    public String getTime() {
        return LocalTime.now().toString();
    }
}
