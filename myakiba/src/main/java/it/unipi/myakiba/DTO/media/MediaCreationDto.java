package it.unipi.myakiba.DTO.media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unipi.myakiba.enumerator.MediaStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnimeCreationDto.class, name = "anime"),
        @JsonSubTypes.Type(value = MangaCreationDto.class, name = "manga")
})
@Data
public abstract class MediaCreationDto {
    @NotBlank
    private String name;

    @NotBlank
    private MediaStatus status;

    @NotBlank
    private List<String> genres;

    private String synopsis;

    private String type;
}