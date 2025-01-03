package it.unipi.myakiba.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersSimilarityDto {
    private String userId;
    private Double similarity;
}
