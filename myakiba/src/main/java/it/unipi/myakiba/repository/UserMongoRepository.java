package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.MonthAnalyticDTO;
import it.unipi.myakiba.model.UserMongo;
import org.bson.Document;
import it.unipi.myakiba.projection.UserBrowseProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {
    @Query("{ 'username': { $regex: ?0, $options: 'i' }, 'id': { $ne: ?1 } }")
    Slice<UserBrowseProjection> findByUsernameContaining(String username, Pageable pageable);
    UserMongo findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Update("{ $addToSet: { followers: ?1 } }")
    void findAndPushFollowerById(String id, String followerId);
    @Update("{ $pull: { followers: ?1 } }")
    void findAndPullFollowerById(String id, String followerId);
    @Aggregation(pipeline = {
            "{ '$match': { '$expr': { '$gt': [ { '$year': '$date' }, ?0 ] } } }",
            "{ '$group': { '_id': { 'year': { '$year': '$date' }, 'month': { '$month': '$date' } }, 'count': { '$sum': 1 } } }",
            "{ '$sort': { '_id.year': 1, 'count': -1 } }",
            "{ '$group': { '_id': '$_id.year', 'maxMonth': { '$first': { 'month': '$_id.month', 'count': '$count' } }, 'year': { '$first': '$_id.year' } } }",
            "{ '$project': { '_id': 0, 'year': '$year', 'month': '$maxMonth.month', 'count': '$maxMonth.count' } }"
    })
    List<MonthAnalyticDto> findMaxMonthByYearGreaterThan(int year);
}
