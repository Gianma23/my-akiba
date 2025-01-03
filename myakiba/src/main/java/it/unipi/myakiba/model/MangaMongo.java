package it.unipi.myakiba.model;

import it.unipi.myakiba.DTO.ReviewDto;
import it.unipi.myakiba.enumerator.MediaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.util.List;

@Document(collection = "manga")
@Data
public class MangaMongo {
    @Id
    @GeneratedValue
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private MediaStatus status;

    @NotBlank
    private int chapters;

    private int sumScores;

    private int numScores;

    @NotBlank
    private List<String> genres;

    private String type;

    private List<String> authors;

    private String synopsis;

    List<ReviewDto> reviews;
}
