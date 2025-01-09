package it.unipi.myakiba.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "anime")
@Data
@EqualsAndHashCode(callSuper = true)
public class AnimeMongo extends MediaMongo{
    @NotBlank
    private int episodes;

    private String source;

    private double duration;

    private String studio;
}
