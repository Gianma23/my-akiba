package it.unipi.myakiba.DTO.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank
    @Min(4)
    private String password;
    @NotBlank
    @Email
    private String email;
}
