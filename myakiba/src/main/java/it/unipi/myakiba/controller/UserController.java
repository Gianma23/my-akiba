package it.unipi.myakiba.controller;

import it.unipi.myakiba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import it.unipi.myakiba.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> browseUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/current-user")
    public ResponseEntity<User> getAuthenticatedUser(@RequestHeader("Authorization") String accessToken) {
/*        return userService.getAuthenticatedUser(accessToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build()); // Unauthorized if token is invalid*/
        return null;
    }
}
