package it.unipi.myakiba.DTO;

import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MediaCreationDto {
    @NotBlank
    private String name;
    @NotBlank
    private MediaStatus status;
    private int chapters;
    private int episodes;
    @NotBlank
    private List<String> genres;

    private int sumScores;
    private int numScores;
    private String type;
    private String synopsis;

    private List<String> authors;   //solo manga

    private String source;      //solo anime
    private double duration;    //
    private String studio;      //

    @NotBlank
    private MediaType mediaType;
}