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
            "{ '$unwind': '$reviews' }",                                            // Fase 1: Unwind per separare le review embedded
            "{ '$group': { " +                                                      // Fase 2: Raggruppamento per anime per calcolare deviazione standard
                    "   '_id': '$_id', " +
                    "   'name': { '$first': '$name' }, " +
                    "   'genres': { '$first': '$genres' }, " +
                    "   'stdDevScore': { '$stdDevPop': '$reviews.score' } " +
                    "} }",
            "{ '$addFields': { 'variance': { '$pow': ['$stdDevScore', 2] } } }",    // Fase 3: Aggiunta del campo varianza (quadrato della deviazione standard)
            "{ '$unwind': '$genres' }",                                             // Fase 4: Dividi l'array dei generi in righe separate
            "{ '$sort': { 'genres': 1, 'variance': -1 } }",                          // Fase 5: Ordinamento per varianza decrescente
            "{ '$group': { " +                                                      // Fase 6: Raggruppamento per genere mantenendo solo l'anime con la varianza massima
                    "   '_id': '$genres', " +
                    "   'anime': { '$first': { 'id': '$_id', 'name': '$name', 'genre': '$genres', 'variance': '$variance' } } " +
                    "} }",
            "{ '$project': { '_id': 0, 'genre': '$anime.genre', 'id': '$anime.id', 'name': '$anime.name' } }"   // Fase 7: Proiezione finale per ritornare solo i campi richiesti
    })
    List<ControversialMediaDto> findTopVarianceAnime();
    @Aggregation(pipeline = {
            "{ '$unwind': '$reviews' }",                                // Fase 1: Unwind per separare le review embedded
            "{ '$group': { " +
                    "   '_id': '$_id', " +
                    "   'name': { '$first': '$name' }, " +
                    "   'averageScore': { '$avg': '$reviews.score' }, " +
                    "   'reviews': { '$push': { 'score': '$reviews.score', 'date': '$reviews.timestamp' } } " +
                    "} }",
            "{ '$addFields': { 'reviews': { '$slice': { '$reverseArray': { '$sortArray': { 'input': '$reviews', 'sortBy': { 'date': -1 } } }, 'n': 5 } } } }",      // Fase 2: Ordina le recensioni dalla più recente alla più vecchia
            "{ '$addFields': { 'recentAverageScore': { '$avg': '$reviews.score' } } }",     // Fase 3: Calcola la media dei primi 5 score
            "{ '$match': { '$expr': { '$lt': ['$recentAverageScore', '$averageScore'] } } }",   // Fase 4: Tieni solo gli anime con media recente < media totale
            "{ '$addFields': { 'scoreDifference': { '$subtract': ['$averageScore', '$recentAverageScore'] } } }",       // Fase 5: Calcola la differenza tra media totale e media recente
            "{ '$sort': { 'scoreDifference': -1 } }",       // Fase 6: Ordina per differenza decrescente
            "{ '$limit': 10 }",     // Fase 7: Limita ai primi 10 anime
            "{ '$project': { '_id': 0, 'id': '$_id', 'name': '$name', 'scoreDifference': 1 } }"     // Fase 8: Proietta i campi richiesti
    })
    List<TrendingMediaDto> findTopDecliningAnime();
    @Aggregation(pipeline = {
            "{ '$unwind': '$reviews' }",                                // Fase 1: Unwind per separare le review embedded
            "{ '$group': { " +
                    "   '_id': '$_id', " +
                    "   'name': { '$first': '$name' }, " +
                    "   'averageScore': { '$avg': '$reviews.score' }, " +
                    "   'reviews': { '$push': { 'score': '$reviews.score', 'date': '$reviews.timestamp' } } " +
                    "} }",
            "{ '$addFields': { 'reviews': { '$slice': { '$reverseArray': { '$sortArray': { 'input': '$reviews', 'sortBy': { 'date': -1 } } }, 'n': 5 } } } }",      // Fase 2: Ordina le recensioni dalla più recente alla più vecchia
            "{ '$addFields': { 'recentAverageScore': { '$avg': '$reviews.score' } } }",     // Fase 3: Calcola la media dei primi 5 score
            "{ '$match': { '$expr': { '$gt': ['$recentAverageScore', '$averageScore'] } } }",   // Fase 4: Tieni solo gli anime con media recente < media totale
            "{ '$addFields': { 'scoreDifference': { '$subtract': ['$recentAverageScore', '$averageScore'] } } }",       // Fase 5: Calcola la differenza tra media totale e media recente
            "{ '$sort': { 'scoreDifference': -1 } }",       // Fase 6: Ordina per differenza decrescente
            "{ '$limit': 10 }",     // Fase 7: Limita ai primi 10 anime
            "{ '$project': { '_id': 0, 'id': '$_id', 'name': '$name', 'scoreDifference': 1 } }"     // Fase 8: Proietta i campi richiesti
    })
    List<TrendingMediaDto> findTopImprovingAnime();

    @Aggregation(pipeline = {
            "{ '$addFields': { 'averageScore': { '$cond': { if: { '$gt': ['$numScores', 0] }, then: { '$divide': ['$sumScores', '$numScores'] }, else: 0 } } } }",
            "{ '$match': { '$expr': { '$or': [ { '$eq': [?0, null] }, { '$in': [?0, '$genres'] } ] } } }",
            "{ '$sort': { 'averageScore': -1 } }",
            "{ '$limit': 10 }",
            "{ '$project': { 'id': 1, 'name': 1, 'averageScore': 1 } }"
    })
    List<MediaAverageDto> findTop10Anime(String genre);
}
