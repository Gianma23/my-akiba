package it.unipi.myakiba.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.myakiba.DTO.media.MediaListsDto;
import it.unipi.myakiba.DTO.user.MediaListUpdateDto;
import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import it.unipi.myakiba.DTO.user.UserNoPwdDto;
import it.unipi.myakiba.DTO.user.UserUpdateDto;
import it.unipi.myakiba.model.UserPrincipal;
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
    public ResponseEntity<?> browseUsers(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(0) @Max(100) int size
    ) {
        Slice<UserIdUsernameDto> results = userService.getUsers(username, page, size);
        if (results.isEmpty()) {
            return ResponseEntity.ok("No user found with this name");
        } else
            return ResponseEntity.ok(results);
    }

    @GetMapping("/user")
    public ResponseEntity<UserNoPwdDto> getUser() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserMongo userMongo = user.getUser();
        UserNoPwdDto userNoPwdDto = new UserNoPwdDto(userMongo.getUsername(), userMongo.getEmail(), userMongo.getBirthdate(), userMongo.getPrivacyStatus());
        return ResponseEntity.ok(userNoPwdDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserNoPwdDto> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId, true));
    }

    @PatchMapping("/user")
    public ResponseEntity<UserNoPwdDto> updateUser(@RequestBody UserUpdateDto updates) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserNoPwdDto updatedUser = userService.updateUser(user.getUser(), updates);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user")
    public ResponseEntity<UserNoPwdDto> deleteUser() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserNoPwdDto deletedUser = userService.deleteUser(user.getUser());
        return ResponseEntity.ok(deletedUser);
    }

    /* ================================ LISTS CRUD ================================ */

    @GetMapping("/user/lists/{mediaType}")
    public ResponseEntity<MediaListsDto> getUserLists(@PathVariable MediaType mediaType) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserLists(user.getUser().getId(), mediaType));
    }

    @GetMapping("/user/{userId}/lists/{mediaType}")
    public ResponseEntity<MediaListsDto> getUserListsById(@PathVariable String userId, @PathVariable MediaType mediaType) {
        return ResponseEntity.ok(userService.getUserLists(userId, mediaType));
    }

    @PostMapping("/user/lists/{mediaType}/{mediaId}")
    public ResponseEntity<String> addMediaToUserList(@PathVariable MediaType mediaType, @PathVariable String mediaId) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.addMediaToUserList(user.getUser().getId(), mediaId, mediaType));
    }

    @PatchMapping("/user/lists/{mediaType}/{mediaId}")
    public ResponseEntity<String> modifyMediaInUserList(@PathVariable MediaType mediaType, @PathVariable String mediaId, @RequestBody MediaListUpdateDto progress) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.modifyMediaInUserList(user.getUser().getId(), mediaId, mediaType, progress.progress()));
    }

    @DeleteMapping("/user/lists/{mediaType}/{mediaId}")
    public ResponseEntity<String> removeMediaFromUserList(@PathVariable MediaType mediaType, @PathVariable String mediaId) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.removeMediaFromUserList(user.getUser().getId(), mediaId, mediaType));
    }

    /* ================================ FOLLOWERS CRUD ================================ */

    @GetMapping("/user/followers")
    public ResponseEntity<List<UserIdUsernameDto>> getUserFollowers() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserFollowers(user.getUser().getId()));
    }

    @GetMapping("/user/{userId}/followers")
    public ResponseEntity<List<UserIdUsernameDto>> getUserFollowersById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserFollowers(userId));
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<UserIdUsernameDto>> getUserFollowing() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUserFollowing(user.getUser().getId()));
    }

    @GetMapping("/user/{userId}/following")
    public ResponseEntity<List<UserIdUsernameDto>> getUserFollowingById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserFollowing(userId));
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
