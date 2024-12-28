package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.UserLoginDto;
import it.unipi.myakiba.DTO.UserRegistrationDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.model.AnimeNeo4j;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class UserService{

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
        return userMongoRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
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
        userMongoRepository.save(newUserMongo);

        UserNeo4j newUserNeo4j = new UserNeo4j();
        System.out.println(newUserMongo.getId());
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

    public Slice<UserMongo> getUsers(String username, int page, int size) {
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

    public List<AnimeNeo4j> getUserLists(String id, String type) {
        return userNeo4jRepository.findListsById(id, type);
    }

    public String addMediaToUserList(String userId, String mediaId) {
        userNeo4jRepository.addMediaToList(userId, mediaId);
        return "Media added to user list";
    }

    public String removeMediaFromUserList(String userId, String mediaId) {
        userNeo4jRepository.removeMediaFromList(userId, mediaId);
        return "Media removed from user list";
    }

    public List<UserNeo4j> getUserFollowers(String id) {
        return userNeo4jRepository.findFollowersById(id);
    }

    public List<UserNeo4j> getUserFollowing(String id) {
        return userNeo4jRepository.findFollowsById(id);
    }

    public String followUser(String followerId, String followedId) {
        userNeo4jRepository.followUser(followerId, followedId);
        return "User followed";
    }

    public String unfollowUser(String followerId, String followedId) {
        userNeo4jRepository.unfollowUser(followerId, followedId);
        return "User unfollowed";
    }
}
