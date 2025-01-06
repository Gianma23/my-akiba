package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.media.AddReviewDto;
import it.unipi.myakiba.DTO.media.MediaIdNameDto;
import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.MediaMongo;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.service.MediaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;

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
    public ResponseEntity<Slice<MediaIdNameDto>> browseMedia(
            @PathVariable MediaType mediaType,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(0) @Max(100) int size) {
        return ResponseEntity.ok(mediaService.browseMedia(mediaType, name, page, size));
    }

    @GetMapping("/{mediaType}/{mediaId}")
    public ResponseEntity<MediaMongo> getMediaById(@PathVariable MediaType mediaType, @PathVariable String mediaId) {
        try {//TODO return DTO with average score
            return ResponseEntity.ok(mediaService.getMediaById(mediaType, mediaId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{mediaType}/{mediaId}/review")
    public ResponseEntity<String> addReview(@PathVariable MediaType mediaType, @PathVariable String mediaId, @RequestBody AddReviewDto review) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok(mediaService.addReview(mediaType, mediaId, user.getUser(), review));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
