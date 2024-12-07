package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
}

