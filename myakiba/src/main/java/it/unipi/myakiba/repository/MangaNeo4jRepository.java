package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.ListCounterAnalyticDto;
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
    WITH list_type, collect({id: manga.id, name: manga.name, count: list_count})[0..10] AS top_manga
    RETURN list_type AS listType, top_manga AS topMedia
    """)
    List<ListCounterAnalyticDto> getMangaAppearancesInLists();
}
