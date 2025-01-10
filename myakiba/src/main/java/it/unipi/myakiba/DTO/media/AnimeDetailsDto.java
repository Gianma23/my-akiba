package it.unipi.myakiba.DTO.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class AnimeDetailsDto extends MediaDetailsDto {
    @NotBlank
    private int episodes;

    @NotEmpty
    private String source;

    @NotEmpty
    private double duration;

    @NotEmpty
    private List<String> studios;
}