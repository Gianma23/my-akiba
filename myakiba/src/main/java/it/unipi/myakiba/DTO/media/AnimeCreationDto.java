package it.unipi.myakiba.DTO.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnimeCreationDto extends MediaCreationDto {
    @NotBlank
    private int episodes;

    @NotEmpty
    private String source;

    @NotEmpty
    private double duration;

    @NotEmpty
    private List<String> studios;
}