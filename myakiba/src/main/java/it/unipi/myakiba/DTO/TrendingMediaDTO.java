package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrendingMediaDTO {
    @NotBlank
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private float difference;
}
