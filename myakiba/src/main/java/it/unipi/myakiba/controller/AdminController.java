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

import org.bson.Document;
import java.util.List;

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
    public ResponseEntity<?> getMonthlyRegistrations() {
        try {
            return ResponseEntity.ok(analyticsService.getMonthlyRegistrations());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/analytics/avgscore")
    public String getAvgScore() {
        try {
            return analyticsService.getAvgScore();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/highestrate")
    public String getHighestRatedMedia() {
        try {
            return analyticsService.getHighestRatedMedia();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/controversial")
    public String getControversialMedia() {
        try {
            return analyticsService.getControversialMedia();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/worse")
    public String getWorseningMedia() {
        try {
            return analyticsService.getWorseningMedia();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/better")
    public String getImprovingMedia() {
        try {
            return analyticsService.getImprovingMedia();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/clique")
    public String getClique() {
        try {
            return analyticsService.getClique();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/influencers")
    public String getInfluencers() {
        try {
            return analyticsService.getInfluencers();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/listcounter")
    public String getListCounter() {
        try {
            return analyticsService.getListCounter();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/mediainlists")
    public String getMediaInLists() {
        try {
            return analyticsService.getMediaInLists();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/analytics/usersonpar")
    public String getUsersOnPar() {
        try {
            return analyticsService.getUsersOnPar();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
