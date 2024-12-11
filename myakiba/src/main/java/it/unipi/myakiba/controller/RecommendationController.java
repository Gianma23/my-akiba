package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.service.RecommendationService;
import it.unipi.myakiba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations", description = "Operations related to recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/similar-users")
    public String getUsersWithSimilarTastes() {
        return "";
    }

    @GetMapping("/popular-among-follows")
    public String getPopularMediaAmongFollows() {
        return "";
    }

    @GetMapping("/best-media/{mediaType}")
    public String getBestMedia(@PathVariable String mediaType, @RequestParam(required = false) String genre) {
        return "";
    }
}
