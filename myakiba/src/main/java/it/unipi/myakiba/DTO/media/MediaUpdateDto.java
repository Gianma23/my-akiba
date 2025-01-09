package it.unipi.myakiba.DTO.media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unipi.myakiba.enumerator.MediaStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnimeUpdateDto.class, name = "anime"),
        @JsonSubTypes.Type(value = MangaUpdateDto.class, name = "manga")
})
@Data
public abstract class MediaUpdateDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private MediaStatus status;

    @NotEmpty
    private List<String> genres;

    @NotEmpty
    private String synopsis;

    @NotEmpty
    private String type;
}