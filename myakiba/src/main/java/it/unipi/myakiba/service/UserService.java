package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.MediaListsDto;
import it.unipi.myakiba.DTO.UserLoginDto;
import it.unipi.myakiba.DTO.UserRegistrationDto;
import it.unipi.myakiba.DTO.ListElementDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.enumerator.PrivacyStatus;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.projection.UserBrowseProjection;
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
import java.util.Map;

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

    public UserMongo getUserById(String id, boolean checkPrivacyStatus) throws UsernameNotFoundException {
        UserMongo user = userMongoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));

        if (!checkPrivacyStatus) {
            return user;
        }

        switch (user.getPrivacyStatus()) {
            case ALL:
                return user;
            case FOLLOWERS:
                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal.getUser().getFollowers().contains(user.getId())) {
                    return user;
                }
                break;
            case NOBODY:
                throw new IllegalArgumentException("User has set their profile to private");
        }
        return null;
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
        newUserMongo.setPrivacyStatus(PrivacyStatus.ALL);
        userMongoRepository.save(newUserMongo);

        UserNeo4j newUserNeo4j = new UserNeo4j();
        newUserNeo4j.setId(newUserMongo.getId());
        newUserNeo4j.setUsername(user.getUsername());
        newUserNeo4j.setEmail(user.getEmail());
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

    public Slice<UserBrowseProjection> getUsers(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userMongoRepository.findByUsernameContaining(username, pageable);
    }

    public UserMongo updateUser(UserMongo user, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "email":
                    user.setEmail((String) value);
                    break;
                case "username":
                    user.setUsername((String) value);
                    break;
                case "birthdate":
                    user.setBirthdate((LocalDate) value);
                    break;
                case "password":
                    user.setPassword(encoder.encode((String) value));
                    break;
                case "privacyStatus":
                    user.setPrivacyStatus((PrivacyStatus) value);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported field: " + key);
            }
        });
        return userMongoRepository.save(user);
    }

    public UserMongo deleteUser(UserMongo user) {
        userMongoRepository.delete(user);
        return user;
    }

    /* ================================ LISTS CRUD ================================ */

    public MediaListsDto getUserLists(String id, MediaType mediaType) {
        List<ListElementDto> mediaList;
        if (mediaType == MediaType.ANIME) {
            mediaList = userNeo4jRepository.findAnimeListsById(id);
        } else {
            mediaList = userNeo4jRepository.findMangaListsById(id);
        }

        MediaListsDto mediaLists = new MediaListsDto();
        mediaLists.setPlannedList(new ArrayList<>());
        mediaLists.setInProgressList(new ArrayList<>());
        mediaLists.setCompletedList(new ArrayList<>());

        for (ListElementDto element : mediaList) {
            if (element.getProgress() == 0) {
                mediaLists.getPlannedList().add(element);
            } else if (element.getProgress() < element.getTotal() && element.getStatus() != MediaStatus.COMPLETE) {
                mediaLists.getInProgressList().add(element);
            } else {
                mediaLists.getCompletedList().add(element);
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

    public String removeMediaFromUserList(String userId, String mediaId) {
        userNeo4jRepository.removeMediaFromList(userId, mediaId);
        return "Media removed from user list";
    }

    /* ================================ FOLLOWERS CRUD ================================ */

    public List<UserNeo4j> getUserFollowers(String id) {
        return userNeo4jRepository.findFollowersById(id);
    }

    public List<UserNeo4j> getUserFollowing(String id) {
        return userNeo4jRepository.findFollowedById(id);
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
}
