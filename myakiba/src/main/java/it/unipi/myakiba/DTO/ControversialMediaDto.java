package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ControversialMediaDto {
    @NotBlank
    private String genre;
    @NotBlank
    private String id;
    @NotBlank
    private String name;
}