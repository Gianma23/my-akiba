package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.UserLoginDto;
import it.unipi.myakiba.DTO.UserRegistrationDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService{

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(AuthenticationManager authManager, UserRepository userRepository, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public List<UserMongo> getUsers() {
        return userRepository.findAll();
    }

    public UserMongo getUserById(String id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
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
        userRepository.save(newUserMongo);

        //TODO add also on neo4j
    }

    public String loginUser(UserLoginDto user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (auth.isAuthenticated()) {
            UserMongo userMongo = userRepository.findByEmail(user.getEmail());
            return JwtUtils.generateToken(userMongo.getId());
        }
        return null;
    }


}
