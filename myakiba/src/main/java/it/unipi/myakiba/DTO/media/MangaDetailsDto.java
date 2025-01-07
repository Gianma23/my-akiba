package it.unipi.myakiba.DTO.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class MangaDetailsDto extends MediaDetailsDto {
    private int chapters;

    private List<String> authors;
}