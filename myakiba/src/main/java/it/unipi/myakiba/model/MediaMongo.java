package it.unipi.myakiba.model;

import it.unipi.myakiba.DTO.media.ReviewDto;
import it.unipi.myakiba.enumerator.MediaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.util.List;

@Data
public abstract class MediaMongo {
    @Id
    @GeneratedValue
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private MediaStatus status;

    @NotBlank
    private int sumScores = 0;

    private int numScores = 0;

    @NotBlank
    private List<String> genres;

    private String type;

    private String synopsis;

    private List<ReviewDto> reviews;
}