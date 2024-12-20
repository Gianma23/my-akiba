package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.LoginResponse;
import it.unipi.myakiba.DTO.UserLoginDto;
import it.unipi.myakiba.DTO.UserRegistrationDto;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import it.unipi.myakiba.model.UserMongo;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<UserMongo> registerUser(@Valid @RequestBody UserRegistrationDto user) {
        userService.registerUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto user) {
        try {
            String token = userService.loginUser(user);
            if (token != null) {
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Slice<UserMongo>> browseUsers(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getUsers(username, page, size));
    }

    @GetMapping("/user")
    public ResponseEntity<UserMongo> getUser() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(user.getUser());
    }

    @PatchMapping("/user")
    public ResponseEntity<UserMongo> updateUser(@RequestBody Map<String, Object> updates) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserMongo updatedUser = userService.updateUser(user.getUser(), updates);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user")
    public ResponseEntity<UserMongo> deleteUser() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserMongo updatedUser = userService.deleteUser(user.getUser());
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/user/lists")
    public ResponseEntity<List<UserNeo4j>> getUserLists() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return null;
    }

    @PostMapping("/user/lists")
    public ResponseEntity<List<UserMongo>> addMediaToUserList() {
        return null;
    }

    @PatchMapping("/user/lists/{mediaId}")
    public ResponseEntity<List<UserMongo>> modifyMediaInUserList(@PathVariable String mediaId) {
        return null;
    }

    @DeleteMapping("/user/lists/{mediaId}")
    public ResponseEntity<List<UserMongo>> removeMediaFromUserList(@PathVariable String mediaId) {
        return null;
    }

    @GetMapping("/user/followers")
    public ResponseEntity<List<UserNeo4j>> getUserFollowers() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserFollowers(user.getUser().getId()));
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<UserMongo>> getUserFollowing() {
        return null;
    }

    @PostMapping("/user/follow/{userId}")
    public ResponseEntity<List<UserMongo>> followUser(@PathVariable String userId) {
        return null;
    }

    @DeleteMapping("/user/follow/{userId}")
    public ResponseEntity<List<UserMongo>> unfollowUser(@PathVariable String userId) {
        return null;
    }
}
