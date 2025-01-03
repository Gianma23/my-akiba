package it.unipi.myakiba.model;

import it.unipi.myakiba.enumerator.MediaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "anime")
@Data
public class AnimeMongo {
    @Id
    @GeneratedValue
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private MediaStatus status;

    @NotBlank
    private int episodes;

    private int sumScores;

    private int numScores;

    @NotBlank
    private List<String> genres;

    private String type;

    private String source;

    private double duration;

    private String studio;

    private String synopsis;

    @Data
    private class Reviews {
        private String userId;
        private String username;
        private int score;
        private String comment;
        private LocalDate timestamp;
    }
}
