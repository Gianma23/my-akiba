package it.unipi.myakiba.DTO.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddReviewDto {
    @NotBlank
    private int score;
    @NotEmpty
    private String comment; //TODO: perchè not empty?? non sarebbe meglio un valore di default?
}
