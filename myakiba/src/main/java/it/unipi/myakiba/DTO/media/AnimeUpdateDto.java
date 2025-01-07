package it.unipi.myakiba.DTO.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnimeUpdateDto extends MediaUpdateDto {
    private int episodes;

    private String source;

    private double duration;

    private String studio;
}