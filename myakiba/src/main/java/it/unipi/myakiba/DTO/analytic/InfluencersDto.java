package it.unipi.myakiba.DTO.analytic;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InfluencersDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String username;
    @NotBlank
    private int followersCount;
}
