package it.unipi.myakiba.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersSimilarityDto {
    private String userId;
    private Double similarity;
}
