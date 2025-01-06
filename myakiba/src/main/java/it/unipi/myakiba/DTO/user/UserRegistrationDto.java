package it.unipi.myakiba.DTO.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

public record UserRegistrationDto (
    @NotBlank
    String username,
    @NotBlank
    @Min(4)
    String password,
    @NotBlank
    @Email
    String email,
    @NotBlank
    @Past
    LocalDate birthdate
) {}
