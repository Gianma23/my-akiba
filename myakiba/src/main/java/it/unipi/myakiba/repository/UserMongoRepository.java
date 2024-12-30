package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.UserMongo;
import it.unipi.myakiba.projection.UserBrowseProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {
    Slice<UserBrowseProjection> findByUsernameContaining(String username, Pageable pageable);
    UserMongo findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Update("{ $push: { followers: ?1 } }")
    void findAndPushFollowerById(String id, String followerId);
    @Update("{ $pull: { followers: ?1 } }")
    void findAndPullFollowerById(String id, String followerId);
}

