package it.unipi.myakiba.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "users")
@Data
public class UserMongo {
    @Id
    private String id;

    @NotBlank(message = "Role cannot be blank")
    private String role;

    @NotBlank(message = "Username cannot be blank")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    @CreatedDate //TODO controllare se funziona
    private LocalDate createdAt;
}