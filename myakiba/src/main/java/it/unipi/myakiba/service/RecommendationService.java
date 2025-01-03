package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.UsersSimilarityDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.projection.UserBrowseProjection;
import it.unipi.myakiba.repository.AnimeMongoRepository;
import it.unipi.myakiba.repository.MangaMongoRepository;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;

    private final MangaMongoRepository mangaMongoRepository;
    private final AnimeMongoRepository animeMongoRepository;

    public RecommendationService(UserMongoRepository userMongoRepository, UserNeo4jRepository userNeo4jRepository, MangaMongoRepository mangaMongoRepository, AnimeMongoRepository animeMongoRepository) {
        this.userMongoRepository = userMongoRepository;
        this.userNeo4jRepository = userNeo4jRepository;
        this.mangaMongoRepository = mangaMongoRepository;
        this.animeMongoRepository = animeMongoRepository;
    }

    public List<UsersSimilarityDto> getUsersWithSimilarTastes(String userId) {
        return userNeo4jRepository.findUsersWithSimilarTastes(userId);
    }

    public List<?> getTop10Media(MediaType mediaType, String genre) {
        if (genre == null) {
            if (mediaType == MediaType.ANIME) {
                return animeMongoRepository.findTop10Anime();
            } else if (mediaType == MediaType.MANGA) {
                return mangaMongoRepository.findTop10Manga();
            }
        }
        else {
            if (mediaType == MediaType.ANIME) {
                return animeMongoRepository.findTop10AnimeByGenre(genre);
            } else if (mediaType == MediaType.MANGA) {
                return mangaMongoRepository.findTop10MangaByGenre(genre);
            }
        }
        return null;
    }
}
