package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.ControversialMediaDto;
import it.unipi.myakiba.model.Manga;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MangaMongoRepository extends MongoRepository<Manga, String> {

    @Aggregation(pipeline = {
            "{ '$unwind': '$reviews' }",                                            // Fase 1: Unwind per separare le review embedded
            "{ '$group': { " +                                                      // Fase 2: Raggruppamento per manga per calcolare deviazione standard
                    "   '_id': '$_id', " +
                    "   'name': { '$first': '$name' }, " +
                    "   'genre': { '$first': '$genre' }, " +
                    "   'stdDevScore': { '$stdDevPop': '$reviews.score' } " +
                    "} }",
            "{ '$addFields': { 'variance': { '$pow': ['$stdDevScore', 2] } } }",    // Fase 3: Aggiunta del campo varianza (quadrato della deviazione standard)
            "{ '$sort': { 'genre': 1, 'variance': -1 } }",                          // Fase 4: Ordinamento per varianza decrescente
            "{ '$group': { " +                                                      // Fase 5: Raggruppamento per genere mantenendo solo il manga con la varianza massima
                    "   '_id': '$genre', " +
                    "   'manga': { '$first': { 'id': '$_id', 'name': '$name', 'genre': '$genre', 'variance': '$variance' } } " +
                    "} }",
            "{ '$project': { '_id': 0, 'id': '$manga.id', 'name': '$manga.name', 'genre': '$manga.genre' } }"   // Fase 6: Proiezione finale per ritornare solo i campi richiesti
    })
    List<ControversialMediaDto> findTopVarianceMangaByGenre();
}
