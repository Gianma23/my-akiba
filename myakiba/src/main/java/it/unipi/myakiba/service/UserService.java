package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.media.MediaListsDto;
import it.unipi.myakiba.DTO.user.*;
import it.unipi.myakiba.DTO.media.ListElementDto;
import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.repository.AnimeMongoRepository;
import it.unipi.myakiba.repository.MangaMongoRepository;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

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

    public UserNoPwdDto getUserById(String id, boolean checkPrivacyStatus) {
        UserMongo user = userMongoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));

        UserNoPwdDto userNoPwdDto = new UserNoPwdDto(user.getUsername(), user.getEmail(), user.getBirthdate(), user.getPrivacyStatus());

        if (!checkPrivacyStatus) {
            return userNoPwdDto;
        }
        return canReturnPrivateDetails(user) ? userNoPwdDto : new UserNoPwdDto(user.getUsername(), null, null, null);
    }

    public Slice<UserIdUsernameDto> getUsers(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userMongoRepository.findByUsernameContaining(username, pageable);
    }

    @Retryable(
            retryFor = {DataAccessException.class, TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public UserNoPwdDto updateUser(UserMongo user, UserUpdateDto updates) {
        UserNeo4j userNeo4j = userNeo4jRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (updates.username() != null) {
            if (userMongoRepository.existsByUsername(updates.username())) {
                throw new IllegalArgumentException("Username already exists");
            }
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
        userNeo4jRepository.save(userNeo4j);
        userMongoRepository.save(user);

        if (updates.username() != null) {
            //updates reviews username
            animeMongoRepository.updateReviewsByUsername(user.getUsername(), updates.username());
            mangaMongoRepository.updateReviewsByUsername(user.getUsername(), updates.username());
        }

        return new UserNoPwdDto(user.getUsername(), user.getEmail(), user.getBirthdate(), user.getPrivacyStatus());
    }

    @Retryable(
            retryFor = {DataAccessException.class, TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public UserNoPwdDto deleteUser(UserMongo user) {
        userNeo4jRepository.deleteById(user.getId());

        userMongoRepository.delete(user);
        animeMongoRepository.deleteReviewsByUsername(user.getUsername());
        mangaMongoRepository.deleteReviewsByUsername(user.getUsername());
        userMongoRepository.deleteUserFromFollowers(user.getId());

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

    @Retryable(
            retryFor = {DataAccessException.class, TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String addMediaToUserList(String userId, String mediaId, MediaType mediaType) {
        userNeo4jRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.addAnimeToList(userId, mediaId);
        } else {
            success = userNeo4jRepository.addMangaToList(userId, mediaId);
        }

        if (!success) {
            throw new NoSuchElementException("Media not found");
        }
        return "Media added to user list";
    }

    public String modifyMediaInUserList(String userId, String mediaId, MediaType mediaType, int progress) {
        userNeo4jRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.modifyAnimeInList(userId, mediaId, progress);
        } else {
            success = userNeo4jRepository.modifyMangaInList(userId, mediaId, progress);
        }

        if (!success) {
            throw new IllegalArgumentException("Cannot update media progress");
        }
        return "Media modified in user list";
    }

    @Retryable(
            retryFor = {DataAccessException.class, TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String removeMediaFromUserList(String userId, String mediaId, MediaType mediaType) {
        userNeo4jRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        boolean success;
        if (mediaType == MediaType.ANIME) {
            success = userNeo4jRepository.removeAnimeFromList(userId, mediaId);
        } else {
            success = userNeo4jRepository.removeMangaFromList(userId, mediaId);
        }

        if (!success) {
            throw new NoSuchElementException("Media not found");
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

    @Retryable(
            retryFor = {DataAccessException.class, TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String followUser(String followerId, String followedId) {
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

    @Retryable(
            retryFor = {DataAccessException.class, TransactionSystemException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
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
                return principal.getUser().getFollowers() != null && principal.getUser().getFollowers().contains(user.getId());
            case NOBODY:
                return false;
        }
        return false;
    }
}
