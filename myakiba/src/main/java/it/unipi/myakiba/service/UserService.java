package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.media.MediaListsDto;
import it.unipi.myakiba.DTO.user.*;
import it.unipi.myakiba.DTO.media.ListElementDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.enumerator.PrivacyStatus;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.repository.AnimeMongoRepository;
import it.unipi.myakiba.repository.MangaMongoRepository;
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
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final AuthenticationManager authManager;
    private final UserMongoRepository userMongoRepository;
    private final PasswordEncoder encoder;
    private final UserNeo4jRepository userNeo4jRepository;
    private final AnimeMongoRepository animeMongoRepository;
    private final MangaMongoRepository mangaMongoRepository;

    @Autowired
    public UserService(AuthenticationManager authManager, UserMongoRepository userMongoRepository, PasswordEncoder encoder, UserNeo4jRepository userNeo4jRepository, AnimeMongoRepository animeMongoRepository, MangaMongoRepository mangaMongoRepository) {
        this.authManager = authManager;
        this.userMongoRepository = userMongoRepository;
        this.encoder = encoder;
        this.userNeo4jRepository = userNeo4jRepository;
        this.animeMongoRepository = animeMongoRepository;
        this.mangaMongoRepository = mangaMongoRepository;
    }

    /* ================================ AUTHENTICATION ================================ */

    public void registerUser(UserRegistrationDto user) {
        if (userMongoRepository.existsByUsername(user.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userMongoRepository.existsByEmail(user.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserMongo newUserMongo = new UserMongo();
        newUserMongo.setUsername(user.username());
        newUserMongo.setPassword(encoder.encode(user.password()));
        newUserMongo.setEmail(user.email());
        newUserMongo.setBirthdate(user.birthdate());
        newUserMongo.setRole("USER");
        newUserMongo.setCreatedAt(LocalDate.now());
        newUserMongo.setPrivacyStatus(PrivacyStatus.ALL);
        userMongoRepository.save(newUserMongo);

        UserNeo4j newUserNeo4j = new UserNeo4j();
        newUserNeo4j.setId(newUserMongo.getId());
        newUserNeo4j.setUsername(user.username());
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

    public UserNoPwdDto getUserById(String id, boolean checkPrivacyStatus) throws NoSuchElementException {
        UserMongo user = userMongoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

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
        UserNeo4j userNeo4j = userNeo4jRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (updates.username() != null) {
            if (userMongoRepository.existsByUsername(updates.username())) {
                throw new IllegalArgumentException("Username already exists");
            }
            //updates reviews username
            animeMongoRepository.updateReviewsByUsername(user.getUsername(), updates.username());
            mangaMongoRepository.updateReviewsByUsername(user.getUsername(), updates.username());
            user.setUsername(updates.username());
            userNeo4j.setUsername(user.getUsername());
        }
        if (updates.password() != null) {
            user.setPassword(encoder.encode(updates.password()));
        }
        if (updates.email() != null) {
            if (userMongoRepository.existsByEmail(updates.email())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(updates.email());
        }
        if (updates.birthdate() != null) {
            user.setBirthdate(updates.birthdate());
        }
        if (updates.privacyStatus() != null) {
            user.setPrivacyStatus(updates.privacyStatus());
            userNeo4j.setPrivacyStatus(user.getPrivacyStatus());
        }
        userMongoRepository.save(user);
        userNeo4jRepository.save(userNeo4j);

        return new UserNoPwdDto(user.getUsername(), user.getEmail(), user.getBirthdate(), user.getPrivacyStatus());
    }

    public UserNoPwdDto deleteUser(UserMongo user) {
        userMongoRepository.delete(user);

        animeMongoRepository.deleteReviewsByUsername(user.getUsername());
        mangaMongoRepository.deleteReviewsByUsername(user.getUsername());
        userMongoRepository.deleteUserFromFollowers(user.getId());

        userNeo4jRepository.deleteById(user.getId());
        return new UserNoPwdDto(user.getUsername(), user.getEmail(), user.getBirthdate(), user.getPrivacyStatus());
    }

    /* ================================ LISTS CRUD ================================ */

    public MediaListsDto getUserLists(String id, MediaType mediaType) {
        userNeo4jRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        List<ListElementDto> mediaList;
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (mediaType == MediaType.ANIME) {
            mediaList = userNeo4jRepository.findAnimeListsById(id, principal.getUser().getId());
        } else if (mediaType == MediaType.MANGA) {
            mediaList = userNeo4jRepository.findMangaListsById(id, principal.getUser().getId());
        } else
            throw new IllegalArgumentException("Media type not found");

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
        userNeo4jRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.addAnimeToList(userId, mediaId);
        } else if (mediaType == MediaType.MANGA) {
            success = userNeo4jRepository.addMangaToList(userId, mediaId);
        } else
            throw new IllegalArgumentException("Media type not found");

        if (!success) {
            throw new IllegalArgumentException("Media not found");
        }
        return "Media added to user list";
    }

    //TODO: controllare che l'utente non scriva di aver visto più episodi di quelli totali (?)
    public String modifyMediaInUserList(String userId, String mediaId, MediaType mediaType, int progress) {
        userNeo4jRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.modifyAnimeInList(userId, mediaId, progress);
        } else  if (mediaType == MediaType.MANGA) {
            success = userNeo4jRepository.modifyMangaInList(userId, mediaId, progress);
        } else
            throw new IllegalArgumentException("Media type not found");

        if (!success) {
            throw new IllegalArgumentException("Media not found");
        }
        return "Media modified in user list";
    }

    public String removeMediaFromUserList(String userId, String mediaId, MediaType mediaType) {
        userNeo4jRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.removeAnimeFromList(userId, mediaId);
        } else if (mediaType == MediaType.MANGA) {
            success = userNeo4jRepository.removeMangaFromList(userId, mediaId);
        } else
            throw new IllegalArgumentException("Media type not found");

        if (!success) {
            throw new IllegalArgumentException("Media not found");
        }
        return "Media deleted in user list";
    }

    /* ================================ FOLLOWERS CRUD ================================ */

    public List<UserIdUsernameDto> getUserFollowers(String id) {
        userNeo4jRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userNeo4jRepository.findFollowersById(id, principal.getUser().getId());
    }

    public List<UserIdUsernameDto> getUserFollowing(String id) {
        userNeo4jRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userNeo4jRepository.findFollowedById(id, principal.getUser().getId());
    }

    public String followUser(String followerId, String followedId) {
        // TODO controllare che non si segua da solo
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("You can't follow yourself");
        }
        boolean success = userNeo4jRepository.followUser(followerId, followedId);
        if (!success) {
            throw new NoSuchElementException("User not found");
        }
        userMongoRepository.findAndPushFollowerById(followedId, followerId);
        return "User followed";
    }

    public String unfollowUser(String followerId, String followedId) {
        boolean success = userNeo4jRepository.unfollowUser(followerId, followedId);
        if (!success) {
            throw new NoSuchElementException("User not found");
        }
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
