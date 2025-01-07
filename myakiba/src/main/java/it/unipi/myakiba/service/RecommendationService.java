package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.media.MediaIdNameDto;
import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.UserNeo4j;
import it.unipi.myakiba.repository.AnimeMongoRepository;
import it.unipi.myakiba.repository.MangaMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RecommendationService {

    private final UserNeo4jRepository userNeo4jRepository;
    private final MangaMongoRepository mangaMongoRepository;
    private final AnimeMongoRepository animeMongoRepository;

    @Autowired
    public RecommendationService(UserNeo4jRepository userNeo4jRepository, MangaMongoRepository mangaMongoRepository, AnimeMongoRepository animeMongoRepository) {
        this.userNeo4jRepository = userNeo4jRepository;
        this.mangaMongoRepository = mangaMongoRepository;
        this.animeMongoRepository = animeMongoRepository;
    }

    public List<UserIdUsernameDto> getUsersWithSimilarTastes(String userId) {
        userNeo4jRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        return userNeo4jRepository.findUsersWithSimilarTastes(userId);
    }

    public List<MediaIdNameDto> getPopularMediaAmongFollows(MediaType mediaType) {
        return userNeo4jRepository.findPopularMediaAmongFollows(mediaType);
    }

    public List<?> getTop10Media(MediaType mediaType, String genre) {
        if (mediaType == MediaType.ANIME) {
            return animeMongoRepository.findTop10Anime(genre);
        } else {
            return mangaMongoRepository.findTop10Manga(genre);
        }
    }
}
