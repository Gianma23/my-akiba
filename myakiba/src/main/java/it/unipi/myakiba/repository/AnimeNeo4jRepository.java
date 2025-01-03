package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.ListCounterAnalyticDto;
import it.unipi.myakiba.model.AnimeNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface AnimeNeo4jRepository extends Neo4jRepository<AnimeNeo4j, String> {
    @Query("""
    MATCH (user:User)-[relationship:LIST_ELEMENT]->(anime:Anime)
    WITH anime,
         CASE
             WHEN relationship.episodesWatched = 0 THEN 'PLANNED'
             WHEN relationship.episodesWatched = anime.episodes AND anime.status = 'COMPLETED' THEN 'COMPLETED'
             ELSE 'IN_PROGRESS'
         END AS listType
    WITH  anime, listType, COUNT(DISTINCT relationship) AS listCount
    ORDER BY listType, listCount DESC
    WITH list_type, collect({id: anime.id, name: anime.name, count: list_count})[0..10] AS top_anime
    RETURN list_type AS listType, top_anime AS topMedia
    """)
    List<ListCounterAnalyticDto> getAnimeAppearancesInLists();
}
