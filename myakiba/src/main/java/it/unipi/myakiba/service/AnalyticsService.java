package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.analytic.*;
import it.unipi.myakiba.DTO.media.MediaInListsAnalyticDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.MonthAnalytic;
import it.unipi.myakiba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final AuthenticationManager authManager;
    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final MonthAnalyticRepository monthAnalyticRepository;
    private final MangaMongoRepository mangaMongoRepository;
    private final AnimeMongoRepository animeMongoRepository;
    private final MangaNeo4jRepository mangaNeo4jRepository;
    private final AnimeNeo4jRepository animeNeo4jRepository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AnalyticsService(AuthenticationManager authManager,
                            UserMongoRepository userMongoRepository, UserNeo4jRepository userNeo4jRepository,
                            MangaMongoRepository mangaMongoRepository, AnimeMongoRepository animeMongoRepository,
                            MangaNeo4jRepository mangaNeo4jRepository, AnimeNeo4jRepository animeNeo4jRepository,
                            MonthAnalyticRepository monthAnalyticRepository,
                            MongoTemplate mongoTemplate) {
        this.authManager = authManager;
        this.userMongoRepository = userMongoRepository;
        this.userNeo4jRepository = userNeo4jRepository;
        this.monthAnalyticRepository = monthAnalyticRepository;
        this.mangaMongoRepository = mangaMongoRepository;
        this.animeMongoRepository = animeMongoRepository;
        this.mangaNeo4jRepository = mangaNeo4jRepository;
        this.animeNeo4jRepository = animeNeo4jRepository;
        this.mongoTemplate = mongoTemplate;
    }

    //   For each year, see the month with most registrations
    public List<MonthAnalyticDto> getMonthlyRegistrations() {
        MonthAnalyticDto maxDocument = monthAnalyticRepository.findTopByOrderByIdDesc();
        int lastYearCalculated = maxDocument != null ? maxDocument.getYear() : 2000;
        // TODO: non mi torna cosa fa (perchè sei gay)
        List<MonthAnalyticDto> results = userMongoRepository.findMaxMonthByYearGreaterThan(lastYearCalculated);
        for (MonthAnalyticDto result : results) {
            MonthAnalytic monthAnalytic = new MonthAnalytic();
            monthAnalytic.setYear(result.getYear());
            monthAnalytic.setMonth(result.getMonth());
            monthAnalytic.setCount(result.getCount());
            monthAnalyticRepository.save(monthAnalytic);
        }
        return mongoTemplate.findAll(MonthAnalyticDto.class, "month_analytics");
    }

    public List<ControversialMediaDto> getControversialMedia(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopVarianceManga();
        } else if (mediaType == MediaType.ANIME) {
            return animeMongoRepository.findTopVarianceAnime();
        } else
            throw new IllegalArgumentException("Invalid media type");
    }

    public List<TrendingMediaDto> getDecliningMedia(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopDecliningManga();
        } else if (mediaType == MediaType.ANIME) {
            return animeMongoRepository.findTopDecliningAnime();
        } else
            throw new IllegalArgumentException("Invalid media type");
    }

    public List<TrendingMediaDto> getImprovingMedia(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopImprovingManga();
        } else if (mediaType == MediaType.ANIME) {
            return animeMongoRepository.findTopImprovingAnime();
        } else
            throw new IllegalArgumentException("Invalid media type");
    }

    public List<CliqueAnalyticDto> getMaxClique() {
        return userNeo4jRepository.findClique();
    }

    public List<InfluencersDto> getInfluencers() {
        return userNeo4jRepository.findMostFollowedUsers();
    }

    public List<ListCounterAnalyticDto> getListCounter(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaNeo4jRepository.findListCounters();
        } else if (mediaType == MediaType.ANIME) {
            return animeNeo4jRepository.findListCounters();
        } else
            throw new IllegalArgumentException("Invalid media type");
    }

    public List<MediaInListsAnalyticDto> getMediaInLists(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaNeo4jRepository.findMangaAppearancesInLists();
        } else if (mediaType == MediaType.ANIME) {
            return animeNeo4jRepository.findAnimeAppearancesInLists();
        } else
            throw new IllegalArgumentException("Invalid media type");
    }
}
