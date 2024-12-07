package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import it.unipi.myakiba.model.User;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/user/login")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.loginUser(user));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> browseUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestHeader("Authorization") String accessToken) {
/*        return userService.getAuthenticatedUser(accessToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build()); // Unauthorized if token is invalid*/
        return null;
    }

    @PatchMapping("/user")
    public ResponseEntity<User> modifyUser(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @DeleteMapping("/user")
    public ResponseEntity<User> deleteUser(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    //@GetMapping("/user/{id}") TODO: capire se mettere /admin o no

    @GetMapping("/user/lists")
    public ResponseEntity<List<User>> getUserLists(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @PostMapping("/user/lists")
    public ResponseEntity<List<User>> addMediaToUserList(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @PatchMapping("/user/lists/{mediaId}")
    public ResponseEntity<List<User>> modifyMediaInUserList(@RequestHeader("Authorization") String accessToken, @PathVariable String mediaId) {
        return null;
    }

    @DeleteMapping("/user/lists/{mediaId}")
    public ResponseEntity<List<User>> removeMediaFromUserList(@RequestHeader("Authorization") String accessToken, @PathVariable String mediaId) {
        return null;
    }

    @GetMapping("/user/followers")
    public ResponseEntity<List<User>> getUserFollowers(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<User>> getUserFollowing(@RequestHeader("Authorization") String accessToken) {
        return null;
    }

    @PostMapping("/user/follow/{userId}")
    public ResponseEntity<List<User>> followUser(@RequestHeader("Authorization") String accessToken, @PathVariable String userId) {
        return null;
    }

    @DeleteMapping("/user/follow/{userId}")
    public ResponseEntity<List<User>> unfollowUser(@RequestHeader("Authorization") String accessToken, @PathVariable String userId) {
        return null;
    }
}
