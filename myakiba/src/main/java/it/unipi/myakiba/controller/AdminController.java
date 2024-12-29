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
import org.springframework.http.RequestEntity;
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
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(mediaService.getMedia(type, name, page, size));
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
    public ResponseEntity<?> getMediaDetails(@PathVariable int mediaId, @RequestParam String type) {
        try {
            return ResponseEntity.ok(mediaService.getMediaDetails(mediaId, type));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  update media info
    @PutMapping("/media/{id}")
    public ResponseEntity<?> updateMedia(@PathVariable int mediaId, @RequestBody String type, @RequestBody MediaCreationDto media) {
        try {
            mediaService.updateMedia(mediaId, type);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  delete a specific media
    @DeleteMapping("/media/{id}")
    public ResponseEntity<?> deleteMedia(@PathVariable int mediaId) {
        try {
            mediaService.deleteMedia(mediaId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  delete a review
    @DeleteMapping("/media/{id}/review/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable int mediaId, @PathVariable int reviewId, @RequestParam String type) {
        try {
            mediaService.deleteReview(mediaId, reviewId, type);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    USER MANAGEMENT
//  get the list of users
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(mediaService.getMedia(username, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  get info on a specific user
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable int userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId, false));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
