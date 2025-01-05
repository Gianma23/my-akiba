package it.unipi.myakiba.DTO.media;

import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MediaIdNameDto {
    private String id;
    private String name;
}