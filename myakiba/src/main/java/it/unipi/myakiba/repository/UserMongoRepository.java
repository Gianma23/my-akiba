package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import it.unipi.myakiba.model.MonthAnalytic;
import it.unipi.myakiba.model.UserMongo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {
    @Query("{ 'username': { $regex: ?0, $options: 'i' }, 'role': 'USER' }")
    Slice<UserIdUsernameDto> findByUsernameContaining(String username, Pageable pageable);

    UserMongo findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Update("{ $addToSet: { followers: ?1 } }")
    void findAndPushFollowerById(String id, String followerId);

    @Update("{ $pull: { followers: ?1 } }")
    void findAndPullFollowerById(String id, String followerId);

    @Query("{ 'followers': ?0 }")
    @Update("{ $pull: { followers: ?0 } }")
    void deleteUserFromFollowers(String id);

    @Aggregation(pipeline = {
            "{ '$match': { 'createdAt': { '$gte': ?0 } } }",
            "{ '$group': { '_id': { 'year': { '$year': '$createdAt' }, 'month': { '$month': '$createdAt' } }, 'count': { '$sum': 1 } } }",
            "{ '$sort': { '_id.year': 1, 'count': -1 } }",
            "{ '$group': { '_id': '$_id.year', 'maxMonth': { '$first': { 'month': '$_id.month', 'count': '$count' } } } }",
            "{ '$project': { '_id': 0, 'year': '$_id', 'month': '$maxMonth.month', 'count': '$maxMonth.count' } }"
    })
    List<MonthAnalytic> findMaxMonthByYearGreaterThan(Date year);
}
