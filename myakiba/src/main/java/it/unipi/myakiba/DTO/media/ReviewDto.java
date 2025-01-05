package it.unipi.myakiba.DTO.media;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDto {
    private String userId;
    private String username;
    private int score;
    private String comment;
    private LocalDate date;
}
