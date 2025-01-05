package it.unipi.myakiba.DTO.media;

import it.unipi.myakiba.enumerator.MediaStatus;
import lombok.Data;

@Data
public class ListElementDto {
    private String id;
    private String name;
    private int progress;
    private int total;
    private MediaStatus status;
}
