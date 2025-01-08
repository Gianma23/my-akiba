package it.unipi.myakiba.DTO.user;

import it.unipi.myakiba.enumerator.PrivacyStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record MediaListUpdateDto(
        @NotBlank @Min(0)
        int progress
){}
