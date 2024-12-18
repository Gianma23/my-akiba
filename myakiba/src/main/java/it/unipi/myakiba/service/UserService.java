package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.UserLoginDto;
import it.unipi.myakiba.DTO.UserRegistrationDto;
import it.unipi.myakiba.config.JwtUtils;
import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import java.util.Map;

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

    public Slice<UserMongo> getUsers(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByUsernameContaining(username, pageable);
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
        return userRepository.save(user);
    }

    public UserMongo deleteUser(UserMongo user) {
        userRepository.delete(user);
        return user;
    }

    public List<UserMongo> getUserLists(String accessToken) {
        return null;
    }
}
