package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.analytic.*;
import it.unipi.myakiba.DTO.media.MediaInListsAnalyticDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.CliqueAnalytic;
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
    private final CliqueAnalyticRepository cliqueAnalyticRepository;
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
                            MonthAnalyticRepository monthAnalyticRepository, CliqueAnalyticRepository cliqueAnalyticRepository,
                            MongoTemplate mongoTemplate) {
        this.authManager = authManager;
        this.userMongoRepository = userMongoRepository;
        this.userNeo4jRepository = userNeo4jRepository;
        this.monthAnalyticRepository = monthAnalyticRepository;
        this.cliqueAnalyticRepository = cliqueAnalyticRepository;
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

    public List<CliqueAnalyticDto> getMaxClique() {
        List<CliqueAnalyticDto> results = userNeo4jRepository.findClique();
        for (CliqueAnalyticDto result : results) {
            CliqueAnalytic cliqueAnalytic = new CliqueAnalytic();
            cliqueAnalytic.setCliqueSize(result.getCliqueSize());
            cliqueAnalytic.setUserDetails(result.getUserDetails());
            cliqueAnalyticRepository.save(cliqueAnalytic);
        }
        //TODO: Return the results in descending order of clique size
        //bisogna fare un'altra funzione in modo da separare
        //la creazione della collezione dalla sua lettura
        //inoltre, la collezione va svuotata prima della creazione
        return results;
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
