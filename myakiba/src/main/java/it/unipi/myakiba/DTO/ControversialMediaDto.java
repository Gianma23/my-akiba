package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ControversialMediaDto {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String genre;
}
