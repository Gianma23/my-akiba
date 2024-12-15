package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.UserMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserMongo, String> {
    UserMongo findByEmail(String email);
}

