package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.AnimeNeo4j;
import it.unipi.myakiba.model.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j, String> {
    @Query("MATCH (u:User) WHERE u.username = $username RETURN u")
    List<AnimeNeo4j> findListsById(String id, String type);

    @Query("MATCH (u:User {id: $userId}), (m:Media {id: $mediaId})" +
            " CREATE (u)-[:LIST_ELEMENT {episodesWatched: 0}]->(m)")
    void addMediaToList(String userId, String mediaId);

    @Query("MATCH (u:User {id: $userId})-[r:LIST_ELEMENT]->(m:Media {id: $mediaId}) DELETE r")
    void removeMediaFromList(String userId, String mediaId);

    @Query("MATCH (u:User)<-[:FOLLOW]-(f:User) WHERE u.id = $id RETURN f")
    List<UserNeo4j> findFollowersById(String id);

    @Query("MATCH (u:User)-[:FOLLOW]->(f:User) WHERE u.id = $id RETURN f")
    List<UserNeo4j> findFollowsById(String id);

    @Query("MATCH (u:User {id: $followerId}), (f:User {id: $followedId}) CREATE (u)-[:FOLLOW]->(f)")
    void followUser(String followerId, String followedId);

    @Query("MATCH (u:User {id: $followerId})-[r:FOLLOW]->(f:User {id: $followedId}) DELETE r")
    void unfollowUser(String followerId, String followedId);
}
