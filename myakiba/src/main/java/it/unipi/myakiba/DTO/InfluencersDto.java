package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InfluencersDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String userName;
    @NotBlank
    private int followersCount;
}
