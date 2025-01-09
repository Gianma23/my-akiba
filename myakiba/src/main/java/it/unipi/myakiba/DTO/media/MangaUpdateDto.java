package it.unipi.myakiba.DTO.media;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MangaUpdateDto extends MediaUpdateDto {
    @NotEmpty
    private int chapters;

    @NotEmpty
    private List<String> authors;
}