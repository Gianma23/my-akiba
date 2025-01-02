package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.ListElementDto;
import it.unipi.myakiba.model.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j, String> {
    @Query("MATCH (u:User)-[l:LIST_ELEMENT]->(a:Anime) WHERE u.id = $id " +
            "RETURN a.id, l.progress, a.totalEpisodes as total")
    List<ListElementDto> findAnimeListsById(String id);

    @Query("MATCH (u:User)-[l:LIST_ELEMENT]->(m:Manga) WHERE u.id = $id " +
            "RETURN m.id, l.progress, m.totalEpisodes as total")
    List<ListElementDto> findMangaListsById(String id);

    @Query("MATCH (u:User {id: $userId}), (a:Anime {id: $mediaId})" +
            " MERGE (u)-[:LIST_ELEMENT {progress: 0}]->(a)")
    void addAnimeToList(String userId, String mediaId);

    @Query("MATCH (u:User {id: $userId})-[r:LIST_ELEMENT]->(m:Media {id: $mediaId}) DELETE r")
    void removeMediaFromList(String userId, String mediaId);

    @Query("MATCH (u:User)<-[:FOLLOW]-(f:User) WHERE u.id = $id RETURN f")
    List<UserNeo4j> findFollowersById(String id);

    @Query("MATCH (u:User)-[:FOLLOW]->(f:User) WHERE u.id = $id RETURN f")
    List<UserNeo4j> findFollowedById(String id);

    @Query("MATCH (u:User {id: $followerId}), (f:User {id: $followedId}) MERGE (u)-[:FOLLOW]->(f)")
    void followUser(String followerId, String followedId);

    @Query("MATCH (u:User {id: $followerId})-[r:FOLLOW]->(f:User {id: $followedId}) DELETE r")
    void unfollowUser(String followerId, String followedId);
}
