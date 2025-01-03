package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.UsersSimilarityDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.projection.UserBrowseProjection;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;

    public RecommendationService(UserMongoRepository userMongoRepository, UserNeo4jRepository userNeo4jRepository) {
        this.userMongoRepository = userMongoRepository;
        this.userNeo4jRepository = userNeo4jRepository;
    }

    public List<UsersSimilarityDto> getUsersWithSimilarTastes(String userId) {
        return userNeo4jRepository.findUsersWithSimilarTastes(userId);
    }

    public List<?> getBestMedia(MediaType mediaType, String genre) {
        //TODO: gestire caso che genre sia null
        /*if (mediaType == MediaType.ANIME) {
            return userMongoRepository.getBestAnime(genre);
        } else if (mediaType == MediaType.MANGA) {
            return userMongoRepository.getBestManga(genre);
        }*/
        return null;
    }
}
