package dev.nikosg.poc.aitoolbox1.backend.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SportService {
    public List<String> findFavoriteFootballClubs(String[] names) {
        Map<String, String> teamByName = Map.of(
                "Peter", "Arsenal",
                "Juan", "Barca",
                "Marco", "Inter Milan",
                "Tobias", "Bayern"
        );

        return Arrays.stream(names)
                .map(co -> teamByName.getOrDefault(co, "Unknown"))
                .toList();
    }

    public List<String> findFavoriteBasketBallClubs(List<String> names) {
        Map<String, String> teamByName = Map.of(
                "Peter", "Bulls",
                "Juan", "Lakers",
                "Marco", "Celtics",
                "Tobias", "Rockets"
        );

        return names.stream()
                .map(co -> teamByName.getOrDefault(co, "Unknown"))
                .toList();
    }
}
