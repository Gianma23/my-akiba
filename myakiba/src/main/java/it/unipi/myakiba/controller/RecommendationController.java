package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.media.MediaIdNameDto;
import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
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
    public ResponseEntity<List<UserIdUsernameDto>> getUsersWithSimilarTastes() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(recommendationService.getUsersWithSimilarTastes(user.getUser().getId()));
    }

    @GetMapping("/popular-among-follows/{mediaType}")
    public ResponseEntity<List<MediaIdNameDto>> getPopularMediaAmongFollows(@PathVariable MediaType mediaType) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //TODO perchè non uso user?
        return ResponseEntity.ok(recommendationService.getPopularMediaAmongFollows(mediaType));
    }

    @GetMapping("/top10media/{mediaType}")
    public ResponseEntity<List<?>> getTop10Media(@PathVariable MediaType mediaType, @RequestParam(required = false) String genre) {
        return ResponseEntity.ok(recommendationService.getTop10Media(mediaType, genre));
    }
}
