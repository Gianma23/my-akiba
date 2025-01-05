package it.unipi.myakiba.DTO.media;

import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MediaCreationDto {
    @NotBlank
    private MediaType mediaType;
    @NotBlank
    private String name;
    @NotBlank
    private MediaStatus status;
    @NotBlank
    private List<String> genres;
    private int chapters;
    private int episodes;

    private int sumScores;
    private int numScores;
    private String type;
    private String synopsis;

    private List<String> authors;   //solo manga

    private String source;      //solo anime
    private double duration;    //solo anime
    private String studio;      //solo anime

}