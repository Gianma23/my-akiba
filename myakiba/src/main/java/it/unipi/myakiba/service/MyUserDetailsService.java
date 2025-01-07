package it.unipi.myakiba.service;

import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.model.UserPrincipal;
import it.unipi.myakiba.repository.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserMongoRepository userMongoRepository;

    @Autowired
    public MyUserDetailsService(UserMongoRepository userMongoRepository) {
        this.userMongoRepository = userMongoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserMongo user = userMongoRepository.findByEmail(email);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        return new UserPrincipal(user);
    }

    public UserMongo loadUserById(String id) {
        return userMongoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
    }
}
