package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserLoginDto {
    @NotBlank
    private String password;
    @NotBlank
    @Email
    private String email;
}
