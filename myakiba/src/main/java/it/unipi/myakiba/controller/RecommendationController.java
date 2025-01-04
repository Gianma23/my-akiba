package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.user.UsersSimilarityDto;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.service.RecommendationService;
import it.unipi.myakiba.enumerator.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations", description = "Operations related to recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/similar-users")
    public ResponseEntity<List<UsersSimilarityDto>> getUsersWithSimilarTastes() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(recommendationService.getUsersWithSimilarTastes(user.getUser().getId()));
    }

    @GetMapping("/popular-among-follows")
    public String getPopularMediaAmongFollows() {
        return "";
    }

    @GetMapping("/top10media/{mediaType}")
    public ResponseEntity<List<?>> getTop10Media(@PathVariable MediaType mediaType, @RequestParam(required = false) String genre) {
        return ResponseEntity.ok(recommendationService.getTop10Media(mediaType, genre));
    }
}
