package it.unipi.myakiba.DTO.user;

import it.unipi.myakiba.enumerator.PrivacyStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

public record UserNoPwdDto(
        @NotBlank String username,
        @NotBlank @Email String email,
        @Past Date birthdate,
        PrivacyStatus privacyStatus) {
}
