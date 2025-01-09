package it.unipi.myakiba.DTO.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class
AnimeUpdateDto extends MediaUpdateDto {
    private int episodes;

    private String source;

    private double duration;

    private String studio;
}