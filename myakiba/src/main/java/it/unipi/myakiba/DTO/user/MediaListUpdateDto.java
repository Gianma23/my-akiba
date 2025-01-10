package it.unipi.myakiba.DTO.user;

import jakarta.validation.constraints.*;

public record MediaListUpdateDto(
        @NotBlank @Min(0)
        int progress
){}
