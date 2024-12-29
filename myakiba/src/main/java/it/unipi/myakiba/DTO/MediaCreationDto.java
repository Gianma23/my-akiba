package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

@Data
public class MediaCreationDto {
    @NotBlank
    private String name;
    @NotBlank
    private float score;
    @NotBlank
    private int episodes;
    @NotBlank
    private String status;
}
