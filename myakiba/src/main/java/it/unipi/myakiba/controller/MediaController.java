package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.media.AddReviewDto;
import it.unipi.myakiba.DTO.media.MediaDetailsDto;
import it.unipi.myakiba.DTO.media.MediaIdNameDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.service.MediaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media management", description = "Operations related to media catalog")
public class MediaController {

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/{mediaType}")
    public ResponseEntity<?> browseMedia(
            @PathVariable MediaType mediaType,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(0) @Max(100) int size) {
        Slice<MediaIdNameDto> results = mediaService.browseMedia(mediaType, name, page, size);
        if (results.isEmpty()) {
            return ResponseEntity.ok("No media found with this name");
        } else
            return ResponseEntity.ok(results);
    }

    @GetMapping("/{mediaType}/{mediaId}")
    public ResponseEntity<MediaDetailsDto> getMediaById(@PathVariable MediaType mediaType, @PathVariable String mediaId) {
        return ResponseEntity.ok(mediaService.getMediaById(mediaType, mediaId)); //TODO return DTO with average score
    }

    @PostMapping("/{mediaType}/{mediaId}/review")
    public ResponseEntity<String> addReview(@PathVariable MediaType mediaType, @PathVariable String mediaId, @RequestBody AddReviewDto review) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(mediaService.addReview(mediaType, mediaId, user.getUser(), review));
    }
}
