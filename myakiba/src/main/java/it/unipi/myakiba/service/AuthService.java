package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.user.UserLoginDto;
import it.unipi.myakiba.DTO.user.UserRegistrationDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.enumerator.PrivacyStatus;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.repository.AnimeMongoRepository;
import it.unipi.myakiba.repository.MangaMongoRepository;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
}
