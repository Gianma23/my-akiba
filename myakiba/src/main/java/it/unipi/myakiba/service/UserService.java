package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.media.MediaListsDto;
import it.unipi.myakiba.DTO.user.*;
import it.unipi.myakiba.DTO.ListElementDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.enumerator.PrivacyStatus;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final AuthenticationManager authManager;
    private final UserMongoRepository userMongoRepository;
    private final PasswordEncoder encoder;
    private final UserNeo4jRepository userNeo4jRepository;

    @Autowired
    public UserService(AuthenticationManager authManager, UserMongoRepository userMongoRepository, PasswordEncoder encoder, UserNeo4jRepository userNeo4jRepository) {
        this.authManager = authManager;
        this.userMongoRepository = userMongoRepository;
        this.encoder = encoder;
        this.userNeo4jRepository = userNeo4jRepository;
    }

    /* ================================ AUTHENTICATION ================================ */

    public void registerUser(UserRegistrationDto user) {
        if (userMongoRepository.existsByUsername((user.getUsername()))) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userMongoRepository.existsByEmail((user.getEmail()))) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserMongo newUserMongo = new UserMongo();
        newUserMongo.setUsername(user.getUsername());
        newUserMongo.setPassword(encoder.encode(user.getPassword()));
        newUserMongo.setEmail(user.getEmail());
        newUserMongo.setBirthdate(user.getBirthdate());
        newUserMongo.setRole("USER");
        newUserMongo.setCreatedAt(LocalDate.now());
        newUserMongo.setPrivacyStatus(PrivacyStatus.ALL);
        userMongoRepository.save(newUserMongo);

        UserNeo4j newUserNeo4j = new UserNeo4j();
        newUserNeo4j.setId(newUserMongo.getId());
        newUserNeo4j.setUsername(user.getUsername());
        newUserNeo4j.setPrivacyStatus(PrivacyStatus.ALL);
        userNeo4jRepository.save(newUserNeo4j);
    }

    public String loginUser(UserLoginDto user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (auth.isAuthenticated()) {
            UserMongo userMongo = userMongoRepository.findByEmail(user.getEmail());
            return JwtUtils.generateToken(userMongo.getId());
        }
        return null;
    }

    /* ================================ USERS CRUD ================================ */

    public UserNoPwdDto getUserById(String id, boolean checkPrivacyStatus) throws UsernameNotFoundException {
        UserMongo user = userMongoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));

        UserNoPwdDto userNoPwdDto = new UserNoPwdDto(user.getUsername(), user.getEmail(), user.getBirthdate(), user.getPrivacyStatus());

        if (!checkPrivacyStatus) {
            return userNoPwdDto;
        }
        return canReturnPrivateDetails(user) ? userNoPwdDto : new UserNoPwdDto(user.getUsername(), null, null, null);
    }

    public Slice<UserIdUsernameDto> getUsers(String username, String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userMongoRepository.findByUsernameContaining(username, userId, pageable);
    }

    public UserNoPwdDto updateUser(UserMongo user, UserUpdateDto updates) {
        if (updates.getUsername() != null) {
            user.setUsername(updates.getUsername());
        }
        if (updates.getPassword() != null) {
            user.setPassword(encoder.encode(updates.getPassword()));
        }
        if (updates.getEmail() != null) {
            user.setEmail(updates.getEmail());
        }
        if (updates.getBirthdate() != null) {
            user.setBirthdate(updates.getBirthdate());
        }
        if (updates.getPrivacyStatus() != null) {
            user.setPrivacyStatus(updates.getPrivacyStatus());
        }
        //TODO: update neo4j user
        userMongoRepository.save(user);
        return new UserNoPwdDto(user.getUsername(), user.getEmail(), user.getBirthdate(), user.getPrivacyStatus());
    }

    public UserNoPwdDto deleteUser(UserMongo user) {
        userMongoRepository.delete(user);
        //TODO: delete neo4j user
        return new UserNoPwdDto(user.getUsername(), user.getEmail(), user.getBirthdate(), user.getPrivacyStatus());
    }

    /* ================================ LISTS CRUD ================================ */

    public MediaListsDto getUserLists(String id, MediaType mediaType) {
        List<ListElementDto> mediaList;
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (mediaType == MediaType.ANIME) {
            mediaList = userNeo4jRepository.findAnimeListsById(id, principal.getUser().getId());
        } else {
            mediaList = userNeo4jRepository.findMangaListsById(id, principal.getUser().getId());
        }

        MediaListsDto mediaLists = new MediaListsDto(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        for (ListElementDto element : mediaList) {
            System.out.println(element);
            if (element.getProgress() == 0) {
                mediaLists.plannedList().add(element);
            } else if (element.getProgress() < element.getTotal() && element.getStatus() != MediaStatus.COMPLETE) {
                mediaLists.inProgressList().add(element);
            } else {
                mediaLists.completedList().add(element);
            }
        }
        return mediaLists;
    }

    public String addMediaToUserList(String userId, String mediaId, MediaType mediaType) {
        if (mediaType == MediaType.ANIME) {
            userNeo4jRepository.addAnimeToList(userId, mediaId);
        } else {
            userNeo4jRepository.addMangaToList(userId, mediaId);
        }
        return "Media added to user list";
    }

    public String modifyMediaInUserList(String userId, String mediaId, MediaType mediaType, int progress) {
        if (mediaType == MediaType.ANIME) {
            userNeo4jRepository.modifyAnimeInList(userId, mediaId, progress);
        } else {
            userNeo4jRepository.modifyMangaInList(userId, mediaId, progress);
        }
        return "Media modified in user list";
    }

    public String removeMediaFromUserList(String userId, String mediaId) {
        userNeo4jRepository.removeMediaFromList(userId, mediaId);
        return "Media removed from user list";
    }

    /* ================================ FOLLOWERS CRUD ================================ */

    public List<UserIdUsernameDto> getUserFollowers(String id) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userNeo4jRepository.findFollowersById(id, principal.getUser().getId());
    }

    public List<UserIdUsernameDto> getUserFollowing(String id) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userNeo4jRepository.findFollowedById(id, principal.getUser().getId());
    }

    public String followUser(String followerId, String followedId) {
        userNeo4jRepository.followUser(followerId, followedId);
        userMongoRepository.findAndPushFollowerById(followedId, followerId);
        return "User followed";
    }

    public String unfollowUser(String followerId, String followedId) {
        userNeo4jRepository.unfollowUser(followerId, followedId);
        userMongoRepository.findAndPullFollowerById(followedId, followerId);
        return "User unfollowed";
    }

    private boolean canReturnPrivateDetails(UserMongo user) {
        switch (user.getPrivacyStatus()) {
            case ALL:
                return true;
            case FOLLOWERS:
                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                return principal.getUser().getFollowers().contains(user.getId());
            case NOBODY:
                return false;
        }
        return false;
    }
}
