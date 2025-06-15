package dev.nikosg.poc.aitoolbox1.tooling.tools;

import dev.nikosg.poc.aitoolbox1.backend.service.SportService;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.Tool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SportTools {

    private final SportService sportService;

    public SportTools(SportService sportService) {
        this.sportService = sportService;
    }

    @Tool(name = "find favorite football clubs", description = "Accepts a list of names and returns a list with their favorite football clubs")
    public List<String> findFavoriteFootballClubs(@ToolParam(value = "Array of names") String[] names) {
        return sportService.findFavoriteFootballClubs(names);
    }

    @Tool(name = "find favorite basketball clubs", description = "Accepts a list of names and returns a list with their favorite basketball clubs")
    public List<String> findFavoriteBasketBallClubs(@ToolParam(value = "List of names") List<String> names) {
        return sportService.findFavoriteBasketBallClubs(names);
    }
}
