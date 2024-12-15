package it.unipi.myakiba.service;

import io.micrometer.observation.ObservationFilter;
import it.unipi.myakiba.model.User;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User registerUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String loginUser(User user) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (auth.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        }
        return null;
    }


}
