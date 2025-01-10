package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.user.UserLoginDto;
import it.unipi.myakiba.DTO.user.UserRegistrationDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.enumerator.PrivacyStatus;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.repository.AnimeMongoRepository;
import it.unipi.myakiba.repository.MangaMongoRepository;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserMongoRepository userMongoRepository;
    private final PasswordEncoder encoder;
    private final UserNeo4jRepository userNeo4jRepository;

    @Autowired
    public AuthService(AuthenticationManager authManager, UserMongoRepository userMongoRepository, PasswordEncoder encoder, UserNeo4jRepository userNeo4jRepository, AnimeMongoRepository animeMongoRepository, MangaMongoRepository mangaMongoRepository) {
        this.authManager = authManager;
        this.userMongoRepository = userMongoRepository;
        this.encoder = encoder;
        this.userNeo4jRepository = userNeo4jRepository;
    }

    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void registerUser(UserRegistrationDto user) {
        if (userMongoRepository.existsByUsername(user.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userMongoRepository.existsByEmail(user.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        String userId = UUID.randomUUID().toString();

        UserNeo4j newUserNeo4j = new UserNeo4j();
        newUserNeo4j.setId(userId);
        newUserNeo4j.setUsername(user.username());
        newUserNeo4j.setPrivacyStatus(PrivacyStatus.ALL);
        userNeo4jRepository.save(newUserNeo4j);

        UserMongo newUserMongo = new UserMongo();
        newUserMongo.setId(userId);
        newUserMongo.setUsername(user.username());
        newUserMongo.setPassword(encoder.encode(user.password()));
        newUserMongo.setEmail(user.email());
        newUserMongo.setBirthdate(user.birthdate());
        newUserMongo.setRole("USER");
        newUserMongo.setCreatedAt(new Date());
        newUserMongo.setPrivacyStatus(PrivacyStatus.ALL);
        userMongoRepository.save(newUserMongo);

    }

    public String loginUser(UserLoginDto user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (auth.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            return JwtUtils.generateToken(userPrincipal.getUser().getId());
        }
        return null;
    }
}
