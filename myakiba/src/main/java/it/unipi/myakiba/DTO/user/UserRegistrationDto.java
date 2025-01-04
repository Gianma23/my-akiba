package it.unipi.myakiba.DTO.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationDto {
    @NotBlank
    private String username;
    @NotBlank
    @Min(4)
    private String password;
    @NotBlank
    @Email
    private String email;
    @Past
    private LocalDate birthdate;
}
