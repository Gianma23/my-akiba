package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.analytic.SCCAnalyticDto;
import it.unipi.myakiba.DTO.analytic.InfluencersDto;
import it.unipi.myakiba.DTO.media.ListElementDto;
import it.unipi.myakiba.DTO.media.MediaIdNameDto;
import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import it.unipi.myakiba.model.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j, String> {
    @Query("""
             MATCH (u:User)-[l:LIST_ELEMENT]->(a:Anime)
             WHERE u.id = $id
               AND (
                 $id = $currentUserId OR
                 u.privacyStatus = 'ALL' OR
                 (u.privacyStatus = 'FOLLOWERS' AND exists {
                     MATCH (follower:User)-[:FOLLOW]->(u)
                     WHERE follower.id = $currentUserId
                 })
               )
             RETURN a.id AS id, a.name AS name, l.progress AS progress, a.episodes AS total, a.status AS status
            """)
    List<ListElementDto> findAnimeListsById(String id, String currentUserId);

    @Query("""
            MATCH (u:User)-[l:LIST_ELEMENT]->(m:Manga)
            WHERE u.id = $id
              AND (
                $id = $currentUserId OR
                u.privacyStatus = 'ALL' OR
                (u.privacyStatus = 'FOLLOWERS' AND exists {
                    MATCH (follower:User)-[:FOLLOW]->(u)
                    WHERE follower.id = $currentUserId
                })
              )
            RETURN m.id AS id, m.name AS name, l.progress AS progress, m.chapters AS total, m.status AS status
            """)
    List<ListElementDto> findMangaListsById(String id, String currentUserId);

    @Query("MATCH (u:User {id: $userId})" +
            " MATCH (a:Anime {id: $mediaId})" +
            " MERGE (u)-[:LIST_ELEMENT {progress: 0}]->(a)" +
            " RETURN count(a) > 0")
    boolean addAnimeToList(String userId, String mediaId);

    @Query("MATCH (u:User {id: $userId})" +
            " MATCH (m:Manga {id: $mediaId})" +
            " MERGE (u)-[:LIST_ELEMENT {progress: 0}]->(m)" +
            "RETURN count(m) > 0")
    boolean addMangaToList(String userId, String mediaId);

    @Query("""
            MATCH (u:User {id: $userId})-[rel:LIST_ELEMENT]->(a:Anime {id: $animeId})
            WHERE $episodesWatched <= a.episodes
            SET rel.progress = $episodesWatched
            RETURN COUNT(a) > 0
            """)
    boolean modifyAnimeInList(String userId, String animeId, int episodesWatched);

    @Query("""
            MATCH (u:User {id: $userId})-[rel:LIST_ELEMENT]->(m:Manga {id: $mangaId})
            WHERE $chaptersRead <= m.chapters
            SET rel.progress = $chaptersRead
            RETURN count(m) > 0
            """)
    boolean modifyMangaInList(String userId, String mangaId, int chaptersRead);

    @Query("MATCH (u:User {id: $userId})-[r:LIST_ELEMENT]->(a:Anime {id: $mediaId})" +
            " DELETE r" +
            " RETURN count(a) > 0")
    boolean removeAnimeFromList(String userId, String mediaId);

    @Query("MATCH (u:User {id: $userId})-[r:LIST_ELEMENT]->(m:Manga {id: $mediaId})" +
            " DELETE r" +
            " RETURN count(m) > 0")
    boolean removeMangaFromList(String userId, String mediaId);

    @Query("""
            MATCH (u:User)<-[:FOLLOW]-(f:User)
            WHERE u.id = $id
              AND (
                $id = $currentUserId OR
                u.privacyStatus = 'ALL' OR
                (u.privacyStatus = 'FOLLOWERS' AND exists {
                    MATCH (follower:User)-[:FOLLOW]->(u)
                    WHERE follower.id = $currentUserId
                })
              )
            RETURN f.id AS id, f.username AS username
            """)
    List<UserIdUsernameDto> findFollowersById(String id, String currentUserId);

    @Query("""
            MATCH (u:User)-[:FOLLOW]->(f:User)
            WHERE u.id = $id
              AND (
                $id = $currentUserId OR
                u.privacyStatus = 'ALL' OR
                (u.privacyStatus = 'FOLLOWERS' AND exists {
                    MATCH (follower:User)-[:FOLLOW]->(u)
                    WHERE follower.id = $currentUserId
                })
              )
            RETURN f.id AS id, f.username AS username
            """)
    List<UserIdUsernameDto> findFollowedById(String id, String currentUserId);

    @Query("MATCH (u:User {id: $followerId}), (f:User {id: $followedId}) MERGE (u)-[:FOLLOW]->(f)" +
            " RETURN count(f) > 0")
    boolean followUser(String followerId, String followedId);

    @Query("MATCH (u:User {id: $followerId})-[r:FOLLOW]->(f:User {id: $followedId}) DELETE r" +
            " RETURN count(f) > 0")
    boolean unfollowUser(String followerId, String followedId);

    @Query("""
            MATCH (u:User {id: $userId})-[:LIST_ELEMENT]->(target)<-[:LIST_ELEMENT]-(other:User)
            WITH collect(u) + collect(other) AS sourceNodes, collect(target) AS targetNodes
            CALL gds.graph.project(
              'myGraph',
              {
                User: {
                  label: 'User'
                }
              },
              {
                LIST_ELEMENT: {
                  type: 'LIST_ELEMENT'
                }
              }
            )
            YIELD graphName
            
            CALL gds.nodeSimilarity.stream('myGraph')
            YIELD node1, node2, similarity
            WITH gds.util.asNode(node2) AS user1, similarity
            WHERE gds.util.asNode(node1).id = $userId
            RETURN user1.id AS id, user1.username AS username, similarity
            ORDER BY similarity DESC
            LIMIT 10
            """)
    List<UserIdUsernameDto> findUsersWithSimilarTastes(String userId);

    @Query("""
            MATCH (user:User {id: $userId})-[:FOLLOW]->(f:User)-[:LIST_ELEMENT]->(media)
            WHERE ((media:Anime AND $mediaType = 'ANIME') OR (media:Manga AND $mediaType = 'MANGA'))
                  AND f.privacyStatus <> 'NOBODY'
            RETURN media.id AS id, media.name AS name, count(media.id) AS count
            ORDER BY count DESC
            LIMIT 10
            """)
    List<MediaIdNameDto> findPopularMediaAmongFollows(String mediaType, String userId);

    @Query("""
            MATCH (u:User)<-[:FOLLOW]-(f:User)
            WITH u, count(f) AS followersCount
            ORDER BY followersCount DESC
            LIMIT 20
            RETURN u.id AS userId, u.username AS username, followersCount
            """)
    List<InfluencersDto> findMostFollowedUsers();

    @Query("""
            MATCH (source:User)-[:FOLLOW]->(target:User)
            WITH collect(source) AS sourceNodes, collect(target) AS targetNodes
            CALL gds.graph.project(
              'graph',
              ['User'],
              {
                FOLLOW: {
                  type: 'FOLLOW'
                }
              }
            )
            YIELD graphName
            
            CALL gds.scc.stream('graph', {})
            YIELD componentId, nodeId
            WITH componentId, collect(gds.util.asNode(nodeId)) AS users
            WHERE size(users) > 1
            RETURN componentId,
                   size(users) AS componentSize,
                   [user IN users | {id: user.id, username: user.username}] AS userDetails
            ORDER BY componentSize DESC
            """)
    List<SCCAnalyticDto> findSCC();

    @Query("CALL gds.graph.drop($graphName) YIELD graphName RETURN graphName")
    void dropGraph(String graphName);
}

