package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.AnimeNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AnimeNeo4jRepository extends Neo4jRepository<AnimeNeo4j, String> {
}
