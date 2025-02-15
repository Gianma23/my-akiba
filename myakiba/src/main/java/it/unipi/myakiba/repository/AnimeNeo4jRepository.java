package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.analytic.ListCounterAnalyticDto;
import it.unipi.myakiba.DTO.media.MediaInListsAnalyticDto;
import it.unipi.myakiba.model.AnimeNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface AnimeNeo4jRepository extends Neo4jRepository<AnimeNeo4j, String> {
    @Query("""
            MATCH (user:User)-[relationship:LIST_ELEMENT]->(anime:Anime)
            WITH anime, relationship,
                 CASE
                     WHEN relationship.progress = 0 THEN 'PLANNED'
                     WHEN relationship.progress = anime.episodes AND anime.status = 'COMPLETE' THEN 'COMPLETED'
                     ELSE 'IN_PROGRESS'
                 END AS listType
            WITH  anime, listType, count(DISTINCT relationship) AS listCount
            ORDER BY listType, listCount DESC
            WITH listType, collect({id: anime.id, name: anime.name, count: listCount})[0..10] AS topMedia
            RETURN listType, topMedia
            """)
    List<ListCounterAnalyticDto> findListCounters();

    @Query("""
            MATCH (user:User)-[relationship:LIST_ELEMENT]->(anime:Anime {id: $animeId})
            WITH anime, relationship,
                 CASE
                     WHEN relationship.progress = 0 THEN 'PLANNED'
                     WHEN relationship.progress = anime.episodes AND anime.status = 'COMPLETE' THEN 'COMPLETED'
                     ELSE 'IN_PROGRESS'
                 END AS listType
            WITH  anime, listType, count(DISTINCT relationship) AS listCount
            WITH anime, collect({listType: listType, listCount: listCount}) AS appearances
            RETURN anime.id AS mediaId, anime.name AS mediaName, appearances
            """)
    List<MediaInListsAnalyticDto> findAnimeAppearancesInLists(String animeId);
}
