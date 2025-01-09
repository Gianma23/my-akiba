package it.unipi.myakiba.DTO.media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "mediaType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnimeCreationDto.class, name = "ANIME"),
        @JsonSubTypes.Type(value = MangaCreationDto.class, name = "MANGA")
})
@Data
public abstract class MediaCreationDto {
    @NotBlank
    private MediaType mediaType;

    @NotBlank
    private String name;

    @NotBlank
    private MediaStatus status;

    @NotBlank
    private List<String> genres;

    private String synopsis;

    private String type;
}