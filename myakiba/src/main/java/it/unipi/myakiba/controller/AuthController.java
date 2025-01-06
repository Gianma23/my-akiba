package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.user.AccessTokenDto;
import it.unipi.myakiba.DTO.user.UserLoginDto;
import it.unipi.myakiba.DTO.user.UserRegistrationDto;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication operations")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /* ================================ AUTHENTICATION ================================ */

    @PostMapping("/register")
    public ResponseEntity<UserMongo> registerUser(@Valid @RequestBody UserRegistrationDto user) {
        userService.registerUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto user) {
        try {
            String token = userService.loginUser(user);
            if (token != null) {
                return ResponseEntity.ok(new AccessTokenDto(token));
            } else try {
                {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }
}
