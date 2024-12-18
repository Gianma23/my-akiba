package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.UserMongo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserMongo, String> {
    Slice<UserMongo> findByUsernameContaining(String username, Pageable pageable);
    UserMongo findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

