package it.unipi.myakiba.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "manga")
@Data
@EqualsAndHashCode(callSuper = true)
public class MangaMongo extends MediaMongo {
    @NotBlank
    private int chapters;

    private List<String> authors;
}
