package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.MangaNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MangaNeo4jRepository extends Neo4jRepository<MangaNeo4j, String> {
}
