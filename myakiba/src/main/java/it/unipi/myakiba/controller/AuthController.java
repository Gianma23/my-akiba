package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.user.AccessTokenDto;
import it.unipi.myakiba.DTO.user.UserLoginDto;
import it.unipi.myakiba.DTO.user.UserRegistrationDto;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.service.AuthService;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication operations")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /* ================================ AUTHENTICATION ================================ */

    @PostMapping("/register")
    public ResponseEntity<UserMongo> registerUser(@Valid @RequestBody UserRegistrationDto user) {
        authService.registerUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto user) {
        try {
            String token = authService.loginUser(user);
            if (token != null)
                return ResponseEntity.ok(new AccessTokenDto(token));
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
