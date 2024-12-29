package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.MediaCreationDto;
import it.unipi.myakiba.model.Anime;
import it.unipi.myakiba.model.Manga;
import it.unipi.myakiba.service.AnalyticsService;
import it.unipi.myakiba.service.MediaService;
import it.unipi.myakiba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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

//    MEDIA MANAGEMENT
//  get the list of media
    @GetMapping("/media")
    public ResponseEntity<?> getMediaList(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(mediaService.getMedia(type, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  add a new media
    @PostMapping("/media/add")
    public ResponseEntity<String> addMedia(@RequestBody MediaCreationDto media) {
        try {
            mediaService.addMedia(media);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  get info on a specific media
    @GetMapping("/media/{id}")
    public String getMediaDetails() {
        return mediaService.getMedia();
    }

//  update media info
    @PutMapping("/media/{id}")
    public String updateMedia() {
        return mediaService.updateMedia();
    }

//  delete a specific media
    @DeleteMapping("/media/{id}")
    public String deleteMedia() {
        return mediaService.deleteMedia();
    }

//  delete a review
    @DeleteMapping("/media/{id}/review/{reviewId}")
    public String deleteReview() {
        return mediaService.deleteReview();
    }

//    USER MANAGEMENT
//  get the list of users
    @GetMapping("/users")
    public String getUsers() {
        return userService.getUsers();
    }

//  get info on a specific user
    @GetMapping("/users/{userId}")
    public String getUserDetails() {
        return userService.getUserById(userId, false);
    }

//    ANALYTICS
    @GetMapping("/analytics/monthlyregistrations")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/avgscore")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/highestrate")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/controversial")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/worse")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/better")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/clique")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/influencers")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/listcounter")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/mediainlists")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }

    @GetMapping("/analytics/usersonpar")
    public String getUsersAnalytics() {
        return analyticsService.getUsersAnalytics();
    }
}
