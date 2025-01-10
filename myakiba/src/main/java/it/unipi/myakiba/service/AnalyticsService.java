package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.analytic.*;
import it.unipi.myakiba.DTO.media.MediaInListsAnalyticDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.MonthAnalytic;
import it.unipi.myakiba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AnalyticsService {

    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final MonthAnalyticRepository monthAnalyticRepository;
    private final MangaMongoRepository mangaMongoRepository;
    private final AnimeMongoRepository animeMongoRepository;
    private final MangaNeo4jRepository mangaNeo4jRepository;
    private final AnimeNeo4jRepository animeNeo4jRepository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AnalyticsService(UserMongoRepository userMongoRepository, UserNeo4jRepository userNeo4jRepository,
                            MangaMongoRepository mangaMongoRepository, AnimeMongoRepository animeMongoRepository,
                            MangaNeo4jRepository mangaNeo4jRepository, AnimeNeo4jRepository animeNeo4jRepository,
                            MonthAnalyticRepository monthAnalyticRepository,
                            MongoTemplate mongoTemplate) {
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
    public List<MonthAnalytic> getMonthlyRegistrations() {
        MonthAnalytic maxDocument = monthAnalyticRepository.findTopByOrderByYearDesc();
        int lastYearCalculated = maxDocument != null ? maxDocument.getYear() + 1 : 2000;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, lastYearCalculated);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date firstDay = cal.getTime();
        List<MonthAnalytic> results = userMongoRepository.findMaxMonthByYearGreaterThan(firstDay);
        monthAnalyticRepository.saveAll(results);
        return monthAnalyticRepository.findAllByOrderByYear();
    }

    public List<ControversialMediaDto> getControversialMedia(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopVarianceManga();
        } else {
            return animeMongoRepository.findTopVarianceAnime();
        }
    }

    public List<TrendingMediaDto> getDecliningMedia(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopDecliningManga();
        } else {
            return animeMongoRepository.findTopDecliningAnime();
        }
    }

    public List<TrendingMediaDto> getImprovingMedia(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopImprovingManga();
        } else {
            return animeMongoRepository.findTopImprovingAnime();
        }
    }

    public List<SCCAnalyticDto> getSCC() {
        List<SCCAnalyticDto> cliques = userNeo4jRepository.findSCC();
        userNeo4jRepository.dropGraph("graph");
        return cliques;
    }

    public List<InfluencersDto> getInfluencers() {
        return userNeo4jRepository.findMostFollowedUsers();
    }

    public List<ListCounterAnalyticDto> getListCounter(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaNeo4jRepository.findListCounters();
        } else {
            return animeNeo4jRepository.findListCounters();
        }
    }

    public List<MediaInListsAnalyticDto> getMediaInLists(MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            return mangaNeo4jRepository.findMangaAppearancesInLists();
        } else {
            return animeNeo4jRepository.findAnimeAppearancesInLists();
        }
    }
}
