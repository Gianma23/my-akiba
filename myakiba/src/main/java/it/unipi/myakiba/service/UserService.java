package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.UserLoginDto;
import it.unipi.myakiba.DTO.UserRegistrationDto;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserService(AuthenticationManager authManager, UserRepository userRepository, JWTService jwtService) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public List<UserMongo> getUsers() {
        return userRepository.findAll();
    }

    public void registerUser(UserRegistrationDto user) {
        if (userRepository.existsByUsername((user.getUsername()))) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail((user.getEmail()))) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserMongo newUserMongo = new UserMongo();
        newUserMongo.setUsername(user.getUsername());
        newUserMongo.setPassword(encoder.encode(user.getPassword()));
        newUserMongo.setEmail(user.getEmail());
        newUserMongo.setBirthdate(user.getBirthdate());
        newUserMongo.setRole("USER");
        newUserMongo.setCreatedAt(LocalDate.now());
        userRepository.save(newUserMongo);
    }

    public String loginUser(UserLoginDto user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        UserMongo userMongo = userRepository.findByEmail(user.getEmail());
        if (auth.isAuthenticated()) {
            return jwtService.generateToken(userMongo.getId());
        }
        return null;
    }


}
