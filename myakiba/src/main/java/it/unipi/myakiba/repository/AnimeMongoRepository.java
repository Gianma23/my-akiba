package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.analytic.ControversialMediaDto;
import it.unipi.myakiba.DTO.analytic.TrendingMediaDto;
import it.unipi.myakiba.DTO.media.MediaAverageDto;
import it.unipi.myakiba.model.AnimeMongo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeMongoRepository extends MongoRepository<AnimeMongo, String> {
    @Aggregation(pipeline = {
            "{ '$match': { 'name': { $regex: ?0, $options: 'i' } } }",
            "{ '$addFields': { 'averageScore': { $cond: { if: { $eq: ['$numScores', 0] }, then: 0, else: { $divide: ['$sumScores', '$numScores'] } } } } }",
            "{ '$project': { 'id': '$_id', 'name': 1, 'averageScore': 1 } }"
    })
    Slice<MediaAverageDto> findByNameContaining(String name, Pageable pageable);

    @Query("{ 'reviews.username': ?0 }")
    @Update("{ '$set': { 'reviews.$.username': ?1 } }")
    void updateReviewsByUsername(String oldUsername, String newUsername);

    @Query("{ 'reviews.username': ?0 }")
    @Update("{ '$pull': { 'reviews': { 'username': ?0 } } }")
    void deleteReviewsByUsername(String username);

    @Aggregation(pipeline = {
            "{ '$addFields': { 'variance': { '$pow': [{ '$stdDevPop': '$reviews.score' }, 2] } } }",
            "{ '$unwind': '$genres' }",
            "{ '$sort': { 'genres': 1, 'variance': -1 } }",
            "{ '$group': { " +
                    "   '_id': '$genres', " +
                    "   'anime': { '$first': { 'id': '$_id', 'name': '$name', 'variance': '$variance' } } " +
                    "} }",
            "{ '$project': { '_id': 0, 'genre': '$_id', 'id': '$anime.id', 'name': '$anime.name' } }"
    })
    List<ControversialMediaDto> findTopVarianceAnime();
    @Aggregation(pipeline = {
            "{ '$addFields': { " +
                    "   'averageScore': { '$cond': { " +
                    "       if: { '$gt': ['$numScores', 0] }, " +
                    "       then: { '$divide': ['$sumScores', '$numScores'] }, " +
                    "       else: 0 " +
                    "   } }, " +
                    "   'reviews': { '$slice': [ { '$sortArray': { 'input': '$reviews', 'sortBy': { 'timestamp': -1 } } }, 5 ] } " +
            "} }",
            "{ '$addFields': { 'recentAverageScore': { '$avg': '$reviews.score' } } }",
            "{ '$match': { '$expr': { '$lt': ['$recentAverageScore', '$averageScore'] } } }",
            "{ '$addFields': { 'scoreDifference': { '$subtract': ['$averageScore', '$recentAverageScore'] } } }",
            "{ '$sort': { 'scoreDifference': -1 } }",
            "{ '$limit': 10 }",
            "{ '$project': { '_id': 0, 'id': '$_id', 'name': '$name', 'scoreDifference': 1 } }"
    })
    List<TrendingMediaDto> findTopDecliningAnime();
    @Aggregation(pipeline = {
            "{ '$addFields': { " +
                    "   'averageScore': { '$cond': { " +
                    "       if: { '$gt': ['$numScores', 0] }, " +
                    "       then: { '$divide': ['$sumScores', '$numScores'] }, " +
                    "       else: 0 " +
                    "   } }, " +
                    "   'reviews': { '$slice': [ { '$sortArray': { 'input': '$reviews', 'sortBy': { 'timestamp': -1 } } }, 5 ] } " +
            "} }",
            "{ '$addFields': { 'recentAverageScore': { '$avg': '$reviews.score' } } }",
            "{ '$match': { '$expr': { '$gt': ['$recentAverageScore', '$averageScore'] } } }",
            "{ '$addFields': { 'scoreDifference': { '$subtract': ['$recentAverageScore', '$averageScore'] } } }",
            "{ '$sort': { 'scoreDifference': -1 } }",
            "{ '$limit': 10 }",
            "{ '$project': { '_id': 0, 'id': '$_id', 'name': '$name', 'scoreDifference': 1 } }"
    })
    List<TrendingMediaDto> findTopImprovingAnime();

    @Aggregation(pipeline = {
            "{ '$match': { '$expr': { '$or': [ { '$eq': [?0, null] }, { '$in': [?0, '$genres'] } ] } } }",
            "{ '$addFields': { 'averageScore': { '$cond': { if: { '$gt': ['$numScores', 0] }, then: { '$divide': ['$sumScores', '$numScores'] }, else: 0 } } } }",
            "{ '$sort': { 'averageScore': -1 } }",
            "{ '$limit': 10 }",
            "{ '$project': { 'id': 1, 'name': 1, 'averageScore': 1 } }"
    })
    List<MediaAverageDto> findTop10Anime(String genre);
}
