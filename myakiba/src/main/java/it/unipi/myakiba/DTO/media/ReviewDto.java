package it.unipi.myakiba.DTO.media;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDto {
    private String userId;      // dato che ogni utente può fare una sola review per media, il suo id fa anche da id per le review
    private String username;
    private int score;
    private String comment;
    private LocalDate timestamp;
}
