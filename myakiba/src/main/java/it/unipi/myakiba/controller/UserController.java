package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.media.MediaListsDto;
import it.unipi.myakiba.DTO.user.UserUpdateDto;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.projection.UserBrowseProjection;
import it.unipi.myakiba.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import it.unipi.myakiba.enumerator.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import it.unipi.myakiba.model.UserMongo;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* ================================ USERS CRUD ================================ */

    @GetMapping("/users")
    public ResponseEntity<Slice<UserBrowseProjection>> browseUsers(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(0) @Max(100) int size
    ) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUsers(username, user.getUser().getId(), page, size));
    }

    @GetMapping("/user")
    public ResponseEntity<UserMongo> getUser() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(user.getUser());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserMongo> getUserById(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId, false));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/user")
    public ResponseEntity<UserMongo> updateUser(@RequestBody UserUpdateDto updates) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserMongo updatedUser = userService.updateUser(user.getUser(), updates);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user")
    public ResponseEntity<UserMongo> deleteUser() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserMongo deletedUser = userService.deleteUser(user.getUser());
        return ResponseEntity.ok(deletedUser);
    }

    /* ================================ LISTS CRUD ================================ */

    @GetMapping("/user/lists/{mediaType}")
    public ResponseEntity<MediaListsDto> getUserLists(@PathVariable MediaType mediaType) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserLists(user.getUser().getId(), mediaType));
    }

    @PostMapping("/user/lists/{mediaType}/{mediaId}")
    public ResponseEntity<String> addMediaToUserList(@PathVariable MediaType mediaType, @PathVariable String mediaId) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.addMediaToUserList(user.getUser().getId(), mediaId, mediaType));
    }

    @PatchMapping("/user/lists/{mediaType}/{mediaId}")
    public ResponseEntity<String> modifyMediaInUserList(@PathVariable MediaType mediaType, @PathVariable String mediaId, @RequestBody @Min(0) int progress) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.addMediaToUserList(user.getUser().getId(), mediaId, mediaType));
    }

    @DeleteMapping("/user/lists/{mediaId}")
    public ResponseEntity<String> removeMediaFromUserList(@PathVariable String mediaId) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.removeMediaFromUserList(user.getUser().getId(), mediaId));
    }

    /* ================================ FOLLOWERS CRUD ================================ */

    @GetMapping("/user/followers")
    public ResponseEntity<List<UserNeo4j>> getUserFollowers() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserFollowers(user.getUser().getId()));
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<UserNeo4j>> getUserFollowing() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserFollowing(user.getUser().getId()));
    }

    @PostMapping("/user/follow/{userId}")
    public ResponseEntity<String> followUser(@PathVariable String userId) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.followUser(user.getUser().getId(), userId));
    }

    @DeleteMapping("/user/follow/{userId}")
    public ResponseEntity<String> unfollowUser(@PathVariable String userId) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.unfollowUser(user.getUser().getId(), userId));
    }
}
