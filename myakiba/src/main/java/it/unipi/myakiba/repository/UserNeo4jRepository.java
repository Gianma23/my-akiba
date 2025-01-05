package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.CliqueAnalyticDto;
import it.unipi.myakiba.DTO.InfluencersDto;
import it.unipi.myakiba.DTO.ListElementDto;
import it.unipi.myakiba.DTO.media.MediaIdNameDto;
import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import it.unipi.myakiba.DTO.user.UsersSimilarityDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.MediaMongo;
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
        (u.privacyStatus = 'FOLLOWERS' AND EXISTS {
            MATCH (follower:User)-[:FOLLOW]->(u)
            WHERE follower.id = $currentUserId
        })
      )
    RETURN a.id as id, a.name as name, l.progress as progress, a.episodes as total, a.status as status
   """)
    List<ListElementDto> findAnimeListsById(String id, String currentUserId);

    @Query("""
    MATCH (u:User)-[l:LIST_ELEMENT]->(m:Manga)
    WHERE u.id = $id
      AND (
        NOT $id = $currentUserId OR
        u.privacyStatus = 'ALL' OR
        (u.privacyStatus = 'FOLLOWERS' AND EXISTS {
            MATCH (follower:User)-[:FOLLOW]->(u)
            WHERE follower.id = $currentUserId
        })
      )
    RETURN m.id as id, m.name as name, l.progress as progress, m.chapters as total, m.status as status
    """)
    List<ListElementDto> findMangaListsById(String id, String currentUserId);

    //TODO: controllare che mediaId esista
    @Query("MATCH (u:User {id: $userId}), (a:Anime {id: $mediaId})" +
            " MERGE (u)-[:LIST_ELEMENT {progress: 0}]->(a)")
    void addAnimeToList(String userId, String mediaId);

    @Query("MATCH (u:User {id: $userId}), (m:Manga {id: $mediaId})" +
            " MERGE (u)-[:LIST_ELEMENT {progress: 0}]->(m)")
    void addMangaToList(String userId, String mediaId);

    @Query("""
        MATCH (u:User {id: $userId})-[rel:LIST_ELEMENT]->(a:Anime {id: $animeId})
        SET rel.progress = $episodesWatched
        RETURN rel
    """)
    void modifyAnimeInList(String userId, String animeId, int episodesWatched);

    @Query("""
        MATCH (u:User {id: $userId})-[rel:LIST_ELEMENT]->(m:Manga {id: $mangaId})
        SET rel.progress = $chaptersRead
        RETURN rel
    """)
    void modifyMangaInList(String userId, String mangaId, int chaptersRead);

    @Query("MATCH (u:User {id: $userId})-[r:LIST_ELEMENT]->(m:Media {id: $mediaId}) DELETE r")
    void removeMediaFromList(String userId, String mediaId);

    @Query("""
    MATCH (u:User)<-[:FOLLOW]-(f:User)
    WHERE u.id = $id
      AND (
        NOT $id = $currentUserId OR
        u.privacyStatus = 'ALL' OR
        (u.privacyStatus = 'FOLLOWERS' AND EXISTS {
            MATCH (follower:User)-[:FOLLOW]->(u)
            WHERE follower.id = $currentUserId
        })
      )
    RETURN f.id as id, f.username as username
    """)
    List<UserIdUsernameDto> findFollowersById(String id, String currentUserId);

    @Query("""
    MATCH (u:User)-[:FOLLOW]->(f:User)
    WHERE u.id = $id
      AND (
        NOT $id = $currentUserId OR
        u.privacyStatus = 'ALL' OR
        (u.privacyStatus = 'FOLLOWERS' AND EXISTS {
            MATCH (follower:User)-[:FOLLOW]->(u)
            WHERE follower.id = $currentUserId
        })
      )
    RETURN f.id as id, f.username as username
    """)
    List<UserIdUsernameDto> findFollowedById(String id, String currentUserId);

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

    @Query("""
    MATCH (user:User)-[:FOLLOWS]->(:User)-[:LIST_ELEMENT]->(media)
    WHERE (media:Anime AND $mediaType = 'ANIME') OR (media:Manga AND $mediaType = 'MANGA')
    RETURN media.id AS id, media.name AS name, COUNT(*) AS popularity
    ORDER BY popularity DESC
    LIMIT 10
    """)
    List<MediaIdNameDto> findPopularMediaAmongFollows(MediaType mediaType);

    @Query("""
        MATCH (u:User)<-[:FOLLOW]-(f:User)
        WITH u, COUNT(f) AS followersCount
        ORDER BY followersCount DESC
        LIMIT 20
        RETURN u.id AS userId, u.name AS username, followersCount
    """)
    List<InfluencersDto> findMostFollowedUsers();

    @Query("""
        CALL gds.graph.project(
          'graph',
          ['User'],
          {
            FOLLOW: {
              type: 'FOLLOW'
            }
          }
        )
        YIELD usersGraph
        CALL gds.scc.stream('usersGraph',{})
        YIELD componentId, nodeId
        WITH componentId, collect(gds.util.asNode(nodeId)) AS users
        RETURN componentId AS cliqueId,
               size(users) AS cliqueSize,
               [user IN users | {id: user.id, name: user.name}] AS userDetails
        CALL gds.graph.drop('graph')
        YIELD graphName
        """)
    List<CliqueAnalyticDto> findClique();
}

