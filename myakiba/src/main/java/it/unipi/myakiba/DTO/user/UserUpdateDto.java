package it.unipi.myakiba.DTO.user;

import it.unipi.myakiba.enumerator.PrivacyStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

public record UserUpdateDto(
        @NotEmpty
        String username,
        @Min(4)
        String password,
        @Email
        String email,
        @Past
        LocalDate birthdate,
        PrivacyStatus privacyStatus
){}
