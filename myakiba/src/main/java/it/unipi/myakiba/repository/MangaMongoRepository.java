package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.Manga;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MangaMongoRepository extends MongoRepository<Manga, String> {

}
