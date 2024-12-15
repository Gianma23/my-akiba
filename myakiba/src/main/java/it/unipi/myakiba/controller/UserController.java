package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.LoginResponse;
import it.unipi.myakiba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import it.unipi.myakiba.model.UserMongo;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<UserMongo> registerUser(@RequestBody UserMongo user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody UserMongo user) {
        try {
            String token = userService.loginUser(user);
            if (token != null) {
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserMongo>> browseUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/user")
    public ResponseEntity<UserMongo> getUser(@RequestHeader("Authorization") String accessToken) {
/*        return userService.getAuthenticatedUser(accessToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build()); // Unauthorized if token is invalid*/
        return null;
    }

    @PatchMapping("/user")
    public ResponseEntity<UserMongo> modifyUser(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @DeleteMapping("/user")
    public ResponseEntity<UserMongo> deleteUser(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    //@GetMapping("/user/{id}") TODO: capire se mettere /admin o no

    @GetMapping("/user/lists")
    public ResponseEntity<List<UserMongo>> getUserLists(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @PostMapping("/user/lists")
    public ResponseEntity<List<UserMongo>> addMediaToUserList(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @PatchMapping("/user/lists/{mediaId}")
    public ResponseEntity<List<UserMongo>> modifyMediaInUserList(@RequestHeader("Authorization") String accessToken, @PathVariable String mediaId) {
        return null;
    }

    @DeleteMapping("/user/lists/{mediaId}")
    public ResponseEntity<List<UserMongo>> removeMediaFromUserList(@RequestHeader("Authorization") String accessToken, @PathVariable String mediaId) {
        return null;
    }

    @GetMapping("/user/followers")
    public ResponseEntity<List<UserMongo>> getUserFollowers(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<UserMongo>> getUserFollowing(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @PostMapping("/user/follow/{userId}")
    public ResponseEntity<List<UserMongo>> followUser(@RequestHeader("Authorization") String accessToken, @PathVariable String userId) {
        return null;
    }

    @DeleteMapping("/user/follow/{userId}")
    public ResponseEntity<List<UserMongo>> unfollowUser(@RequestHeader("Authorization") String accessToken, @PathVariable String userId) {
        return null;
    }
}
