package it.unipi.myakiba.DTO.user;

import it.unipi.myakiba.enumerator.PrivacyStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserNoPwdDto(
        @NotBlank String username,
        @NotBlank @Email String email,
        @Past LocalDate birthdate,
        PrivacyStatus privacyStatus) {
}
