package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.ListElementDto;
import it.unipi.myakiba.DTO.UsersSimilarityDto;
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


    @Query("""
    MATCH (u:User {id: $userId})-[:LIST_ELEMENT]->(target)<-[:LIST_ELEMENT]-(other:User)
    WHERE target:Manga
    WITH collect(u) + collect(other) AS sourceNodes, collect(target) AS targetNodes
    CALL gds.graph.project(
      'myGraph',
      {
        User: {
          label: 'User'
        },
        Manga: {
          label: 'Manga'
        }
      },
      {
        LIST_ELEMENT: {
          type: 'LIST_ELEMENT'
        }
      }
    )
    YIELD graphName
    
    CALL gds.nodeSimilarity.filtered.stream('myGraph')
    YIELD node1, node2, similarity
    WITH gds.util.asNode(node2).username AS userId, similarity
    WHERE gds.util.asNode(node1).id = $userId
    CALL gds.graph.drop('myGraph') YIELD graphName
    RETURN userId, similarity
    ORDER BY similarity DESC
    LIMIT 10
    """)
    List<UsersSimilarityDto> findUsersWithSimilarTastes(String userId);
}

