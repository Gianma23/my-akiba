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

@Service
public class UserService {

    private final UserMongoRepository userMongoRepository;
    private final PasswordEncoder encoder;
    private final UserNeo4jRepository userNeo4jRepository;
    private final AnimeMongoRepository animeMongoRepository;
    private final MangaMongoRepository mangaMongoRepository;

    @Autowired
    public UserService(UserMongoRepository userMongoRepository, PasswordEncoder encoder, UserNeo4jRepository userNeo4jRepository, AnimeMongoRepository animeMongoRepository, MangaMongoRepository mangaMongoRepository) {
        this.userMongoRepository = userMongoRepository;
        this.encoder = encoder;
        this.userNeo4jRepository = userNeo4jRepository;
        this.animeMongoRepository = animeMongoRepository;
        this.mangaMongoRepository = mangaMongoRepository;
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
        if (updates.username() != null) {
            if (userMongoRepository.existsByUsername(updates.username())) {
                throw new IllegalArgumentException("Username already exists");
            }
            //updates reviews username
            animeMongoRepository.updateReviewsByUsername(user.getUsername(), updates.username());
            mangaMongoRepository.updateReviewsByUsername(user.getUsername(), updates.username());
            user.setUsername(updates.username());
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
        }
        userMongoRepository.save(user);

        UserNeo4j userNeo4j = userNeo4jRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userNeo4j.setUsername(user.getUsername());
        userNeo4j.setPrivacyStatus(user.getPrivacyStatus());
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
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.addAnimeToList(userId, mediaId);
        } else {
            success = userNeo4jRepository.addMangaToList(userId, mediaId);
        }

        if (!success) {
            throw new IllegalArgumentException("Media not found");
        }
        return "Media added to user list";
    }

    public String modifyMediaInUserList(String userId, String mediaId, MediaType mediaType, int progress) {
        boolean success;
        //TODO check that progress <= total
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.modifyAnimeInList(userId, mediaId, progress);
        } else {
            success = userNeo4jRepository.modifyMangaInList(userId, mediaId, progress);
        }

        if (!success) {
            throw new IllegalArgumentException("Media not found");
        }
        return "Media modified in user list";
    }

    public String removeMediaFromUserList(String userId, String mediaId, MediaType mediaType) {
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.removeAnimeFromList(userId, mediaId);
        } else {
            success = userNeo4jRepository.removeMangaFromList(userId, mediaId);
        }

        if (!success) {
            throw new IllegalArgumentException("Media not found");
        }
        return "Media deleted in user list";
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
        boolean success = userNeo4jRepository.followUser(followerId, followedId);
        if (!success) {
            throw new IllegalArgumentException("User not found");
        }
        userMongoRepository.findAndPushFollowerById(followedId, followerId);
        return "User followed";
    }

    public String unfollowUser(String followerId, String followedId) {
        boolean success = userNeo4jRepository.unfollowUser(followerId, followedId);
        if (!success) {
            throw new IllegalArgumentException("User not found");
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
