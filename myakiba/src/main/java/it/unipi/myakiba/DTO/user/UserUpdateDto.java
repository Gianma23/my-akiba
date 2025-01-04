package it.unipi.myakiba.DTO.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {
    private String username;
    @Min(4)
    private String password;
    @Email
    private String email;
    @Past
    private LocalDate birthdate;
}
