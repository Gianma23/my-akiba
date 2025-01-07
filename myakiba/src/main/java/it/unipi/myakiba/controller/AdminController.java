package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.analytic.*;
import it.unipi.myakiba.DTO.media.MediaCreationDto;
import it.unipi.myakiba.DTO.media.MediaInListsAnalyticDto;
import it.unipi.myakiba.DTO.media.MediaUpdateDto;
import it.unipi.myakiba.DTO.user.UserNoPwdDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.service.AnalyticsService;
import it.unipi.myakiba.service.MediaService;
import it.unipi.myakiba.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin management", description = "Operations related to admin management")
public class AdminController {
    private final UserService userService;
    private final MediaService mediaService;
    private final AnalyticsService analyticsService;

    @Autowired
    public AdminController(UserService userService, MediaService mediaService, AnalyticsService analyticsService) {
        this.userService = userService;
        this.mediaService = mediaService;
        this.analyticsService = analyticsService;
    }

    /* ================================ MEDIA MANAGEMENT ================================ */

    @PostMapping("/media/{mediaType}")
    public ResponseEntity<String> addMedia(@PathVariable MediaType mediaType, @RequestBody MediaCreationDto media) {
        return ResponseEntity.ok(mediaService.addMedia(mediaType, media));
    }

    @PatchMapping("/media/{mediaType}/{mediaId}")
    public ResponseEntity<String> updateMedia(@PathVariable MediaType mediaType, @PathVariable String mediaId,
                                              @RequestBody MediaUpdateDto updates) {
        return ResponseEntity.ok(mediaService.updateMedia(mediaId, mediaType, updates));
    }

    @DeleteMapping("/media/{mediaType}/{mediaId}")
    public ResponseEntity<String> deleteMedia(@PathVariable MediaType mediaType, @PathVariable String mediaId) {
        return ResponseEntity.ok(mediaService.deleteMedia(mediaId, mediaType));
    }

    @DeleteMapping("/media/{mediaType}/{mediaId}/review/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable MediaType mediaType, @PathVariable String mediaId, @PathVariable String reviewId) {
        return ResponseEntity.ok(mediaService.deleteReview(mediaId, reviewId, mediaType));
    }
    /* ================================ USER MANAGEMENT ================================ */

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserNoPwdDto> getUserDetails(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId, false));
    }

    /* ================================ ANALYTICS ================================ */

    @GetMapping("/analytics/monthlyregistrations")
    public ResponseEntity<List<MonthAnalyticDto>> getMonthlyRegistrations() {
        return ResponseEntity.ok(analyticsService.getMonthlyRegistrations());
    }

    @GetMapping("/analytics/controversial/{mediaType}")
    public ResponseEntity<List<ControversialMediaDto>> getControversialMedia(@PathVariable MediaType mediaType) {
        return ResponseEntity.ok(analyticsService.getControversialMedia(mediaType));

    }

    @GetMapping("/analytics/declining/{mediaType}")
    public ResponseEntity<List<TrendingMediaDto>> getDecliningMedia(@PathVariable MediaType mediaType) {
        return ResponseEntity.ok(analyticsService.getDecliningMedia(mediaType));

    }

    @GetMapping("/analytics/improving/{mediaType}")
    public ResponseEntity<List<TrendingMediaDto>> getImprovingMedia(@PathVariable MediaType mediaType) {
        return ResponseEntity.ok(analyticsService.getImprovingMedia(mediaType));

    }

    @GetMapping("/analytics/max-clique")
    public ResponseEntity<List<CliqueAnalyticDto>> getMaxClique() {
        return ResponseEntity.ok(analyticsService.getMaxClique());

    }

    @GetMapping("/analytics/influencers")
    public ResponseEntity<List<InfluencersDto>> getInfluencers() {
        return ResponseEntity.ok(analyticsService.getInfluencers());

    }

    @GetMapping("/analytics/listcounter/{mediaType}")
    public ResponseEntity<List<ListCounterAnalyticDto>> getListCounter(@PathVariable MediaType mediaType) {
        return ResponseEntity.ok(analyticsService.getListCounter(mediaType));

    }

    @GetMapping("/analytics/mediainlists/{mediaType}")
    public ResponseEntity<List<MediaInListsAnalyticDto>> getMediaInLists(@PathVariable MediaType mediaType) {
        return ResponseEntity.ok(analyticsService.getMediaInLists(mediaType));

    }
}
