package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.MediaCreationDto;
import it.unipi.myakiba.service.AnalyticsService;
import it.unipi.myakiba.service.MediaService;
import it.unipi.myakiba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    MEDIA MANAGEMENT
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

//  update media info
    @PatchMapping("/media/{mediaType}/{mediaId}")
    public ResponseEntity<?> updateMedia(@PathVariable MediaType mediaType, @PathVariable String mediaId, @RequestBody Map<String, Object> updates) {
        try {
            mediaService.updateMedia(mediaId, mediaType, updates);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  delete a specific media
    @DeleteMapping("/media/{mediaId}")
    public ResponseEntity<?> deleteMedia(@PathVariable int mediaId) {
        try {
            mediaService.deleteMedia(mediaId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//  delete a review
    @DeleteMapping("/media/{mediaType}/{mediaId}/review/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable MediaType mediaType, @PathVariable String mediaId, @PathVariable String reviewId) {
        try {
            mediaService.deleteReview(mediaId, reviewId, mediaType);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    USER MANAGEMENT
//  get info on a specific user
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable String userId) {
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

    @GetMapping("/analytics/controversial/{mediaType}")
    public ResponseEntity<?> getControversialMedia(@PathVariable MediaType mediaType) {
        try {
            return ResponseEntity.ok(analyticsService.getControversialMedia(mediaType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/analytics/worse/{mediaType}")
    public ResponseEntity<?> getWorseningMedia(@PathVariable MediaType mediaType) {
        try {
            return ResponseEntity.ok(analyticsService.getWorseningMedia(mediaType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/analytics/better/{mediaType}")
    public ResponseEntity<?> getImprovingMedia(@PathVariable MediaType mediaType) {
        try {
            return ResponseEntity.ok(analyticsService.getImprovingMedia(mediaType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
