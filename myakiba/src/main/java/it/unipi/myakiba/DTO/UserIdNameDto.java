package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserIdNameDto {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
}
