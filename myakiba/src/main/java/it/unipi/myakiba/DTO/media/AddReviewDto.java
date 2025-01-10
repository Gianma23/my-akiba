package it.unipi.myakiba.DTO.media;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddReviewDto {
    @NotBlank @Min(1) @Max(10)
    private int score;
    @NotEmpty
    private String comment;
}
