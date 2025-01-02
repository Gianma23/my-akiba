package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrendingMediaDto {
    @NotBlank
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private float difference;
}
