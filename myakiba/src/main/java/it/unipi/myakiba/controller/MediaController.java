package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media management", description = "Operations related to media catalog")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping("/{type}")
    public String getMedia(@PathVariable String type) {
        try {
            return mediaService.getMedia(type);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
