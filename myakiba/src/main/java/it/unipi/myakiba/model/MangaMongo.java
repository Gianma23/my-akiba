package it.unipi.myakiba.model;

import it.unipi.myakiba.DTO.media.ReviewDto;
import it.unipi.myakiba.enumerator.MediaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.util.List;

@Document(collection = "manga")
@Data
@EqualsAndHashCode(callSuper = true)
public class MangaMongo extends MediaMongo {
    @NotBlank
    private int chapters;

    private List<String> authors;
}
