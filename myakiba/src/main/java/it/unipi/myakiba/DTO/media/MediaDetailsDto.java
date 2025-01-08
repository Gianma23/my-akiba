package it.unipi.myakiba.DTO.media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unipi.myakiba.enumerator.MediaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnimeDetailsDto.class, name = "anime"),
        @JsonSubTypes.Type(value = MangaDetailsDto.class, name = "manga")
})
@Getter
@SuperBuilder
public abstract class MediaDetailsDto {
    private String name;

    private MediaStatus status;

    private double avgScore;

    private List<String> genres;

    private String synopsis;

    private String type;

    private List<ReviewDto> reviews;
}