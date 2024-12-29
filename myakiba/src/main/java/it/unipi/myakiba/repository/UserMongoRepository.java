package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.UserMongo;
import org.bson.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {
    Slice<UserMongo> findByUsernameContaining(String username, Pageable pageable);
    UserMongo findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Aggregation(pipeline = {
            "{ '$match': { '$expr': { '$eq': [ { '$year': '$date' }, ?0 ] } } }",
            "{ '$group': { '_id': { 'month': { '$month': '$date' } }, 'totalCount': { '$sum': '$count' } } }",
            "{ '$sort': { 'totalCount': -1 } }",
            "{ '$limit': 1 }",
            "{ '$project': { '_id': 0, 'month': '$_id.month', 'totalCount': 1 } }"
    })
    Document findMonthWithMaxCountByYear(int year);
}