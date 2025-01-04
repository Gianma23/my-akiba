package it.unipi.myakiba.controller;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.AnimeMongo;
import it.unipi.myakiba.model.MangaMongo;
import it.unipi.myakiba.service.MediaService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.List;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media management", description = "Operations related to media catalog")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping("/{type}")
    public ResponseEntity<?> getMedia(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        try {
            MediaType mediaType = MediaType.fromString(type);

            Page<?> medias = mediaService.getMedia(mediaType, pageable);
            return ResponseEntity.ok(medias);


        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{type}/{mediaId}")
    public ResponseEntity<?> getMediaById(@PathVariable String type, @PathVariable String id){
        try {
            MediaType mediaType = MediaType.fromString(type);
            return mediaService.getMediaById(mediaType, id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
