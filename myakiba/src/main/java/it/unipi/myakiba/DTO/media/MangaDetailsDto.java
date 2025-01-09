package it.unipi.myakiba.DTO.media;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class MangaDetailsDto extends MediaDetailsDto {
    private int chapters;

    private List<String> authors;
}