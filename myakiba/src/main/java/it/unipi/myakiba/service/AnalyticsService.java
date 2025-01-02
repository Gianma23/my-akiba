package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.ControversialMediaDto;
import it.unipi.myakiba.DTO.MonthAnalyticDTO;
import it.unipi.myakiba.DTO.TrendingMediaDTO;
import it.unipi.myakiba.model.MonthAnalytic;
import it.unipi.myakiba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
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

    @Autowired
    public AnalyticsService(AuthenticationManager authManager, UserMongoRepository userMongoRepository, UserNeo4jRepository userNeo4jRepository, MonthAnalyticRepository monthAnalyticRepository, MangaMongoRepository mangaMongoRepository, AnimeMongoRepository animeMongoRepository) {
        this.authManager = authManager;
        this.userMongoRepository = userMongoRepository;
        this.userNeo4jRepository = userNeo4jRepository;
        this.monthAnalyticRepository = monthAnalyticRepository;
        this.mangaMongoRepository = mangaMongoRepository;
        this.animeMongoRepository = animeMongoRepository;
    }
    @Autowired
    private MongoTemplate mongoTemplate;

//   For each year, see the month with most registrations
    public List<MonthAnalyticDTO> getMonthlyRegistrations() throws Exception {
        MonthAnalytic maxDocument = monthAnalyticRepository.findTopByOrderByIdDesc();
        int lastYearCalculated = maxDocument != null ? maxDocument.getYear() : 2000;

        List<MonthAnalyticDTO> results = userMongoRepository.findMaxMonthByYearGreaterThan(lastYearCalculated);
        for(MonthAnalyticDTO result : results) {
            MonthAnalytic monthAnalytic = new MonthAnalytic();
            monthAnalytic.setYear(result.getYear());
            monthAnalytic.setMonth(result.getMonth());
            monthAnalytic.setCount(result.getCount());
            monthAnalyticRepository.save(monthAnalytic);
        }
        return mongoTemplate.findAll(MonthAnalyticDTO.class, "month_analytics");
    }

    public List<ControversialMediaDto> getControversialMedia(MediaType mediaType) throws Exception {
        if(mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopVarianceManga();
        } else {
            return animeMongoRepository.findTopVarianceAnime();
        }
    }

    public List<TrendingMediaDTO> getWorseningMedia(MediaType mediaType) throws Exception {
        if(mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopDecliningManga();
        } else {
            return animeMongoRepository.findTopDecliningAnime();
        }
    }

    public List<TrendingMediaDTO> getImprovingMedia(MediaType mediaType) throws Exception {
        if(mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findTopImprovingManga();
        } else {
            return animeMongoRepository.findTopImprovingAnime();
        }
    }

    public String getClique() throws Exception {
        return "Clique";
    }

    public String getInfluencers() throws Exception {
        return "Influencers";
    }

    public String getListCounter() throws Exception {
        return "List Counter";
    }

    public String getMediaInLists() throws Exception {
        return "Media in Lists";
    }

    public String getUsersOnPar() throws Exception {
        return "Users on Par";
    }
}
