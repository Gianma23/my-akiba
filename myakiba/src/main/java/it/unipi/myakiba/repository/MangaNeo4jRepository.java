package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.ListCounterAnalyticDto;
import it.unipi.myakiba.DTO.MediaInListsAnalyticDto;
import it.unipi.myakiba.model.MangaNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface MangaNeo4jRepository extends Neo4jRepository<MangaNeo4j, String> {
    @Query("""
    MATCH (user:User)-[relationship:LIST_ELEMENT]->(manga:Manga)
    WITH manga,
         CASE
             WHEN relationship.episodesWatched = 0 THEN 'PLANNED'
             WHEN relationship.episodesWatched = manga.episodes AND manga.status = 'COMPLETED' THEN 'COMPLETED'
             ELSE 'IN_PROGRESS'
         END AS listType
    WITH  manga, listType, COUNT(DISTINCT relationship) AS listCount
    ORDER BY listType, listCount DESC
    WITH listType, collect({id: manga.id, name: manga.name, count: listCount})[0..10] AS topMedia
    RETURN listType, topMedia
    """)
    List<ListCounterAnalyticDto> findListCounters();

    @Query("""
    MATCH (user:User)-[relationship:LIST_ELEMENT]->(manga:Manga)
    WITH manga,
         CASE
             WHEN relationship.episodesWatched = 0 THEN 'PLANNED'
             WHEN relationship.episodesWatched = manga.episodes AND manga.status = 'COMPLETED' THEN 'COMPLETED'
             ELSE 'IN_PROGRESS'
         END AS listType
    WITH  manga, listType, COUNT(DISTINCT relationship) AS listCount
    WITH manga, collect({listType: listType, listCount: listCount}) AS appearances
    RETURN manga.id AS mediaId, manga.name AS mediaName, appearances
    """)
    List<MediaInListsAnalyticDto> findMangaAppearancesInLists();
}
