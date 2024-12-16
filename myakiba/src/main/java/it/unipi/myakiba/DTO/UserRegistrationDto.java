package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationDto {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String email;
    @NotNull
    private LocalDate birthdate;
}
