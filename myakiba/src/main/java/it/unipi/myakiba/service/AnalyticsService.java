package it.unipi.myakiba.service;

import it.unipi.myakiba.model.MonthAnalytic;
import it.unipi.myakiba.repository.MonthAnalyticRepository;
import org.bson.Document;
import it.unipi.myakiba.repository.UserMongoRepository;
import it.unipi.myakiba.repository.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class AnalyticsService {

    private final AuthenticationManager authManager;
    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final MonthAnalyticRepository monthAnalyticRepository;

    @Autowired
    public AnalyticsService(AuthenticationManager authManager, UserMongoRepository userMongoRepository, UserNeo4jRepository userNeo4jRepository, MonthAnalyticRepository monthAnalyticRepository) {
        this.authManager = authManager;
        this.userMongoRepository = userMongoRepository;
        this.userNeo4jRepository = userNeo4jRepository;
        this.monthAnalyticRepository = monthAnalyticRepository;
    }
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Document> getMonthlyRegistrations() throws Exception {
            MonthAnalytic maxDocument = monthAnalyticRepository.findTopByOrderByIdDesc();
            int lastYearCalculated = maxDocument != null ? maxDocument.getYear() : 2000;
            int currentYear = Year.now().getValue();

            for(int i = lastYearCalculated+1; i < currentYear; i++) {
                MonthAnalytic monthAnalytic = new MonthAnalytic();
                Document result = userMongoRepository.findMonthWithMaxCountByYear(i);
                if (result != null) {
                    monthAnalytic.setYear(i);
                    monthAnalytic.setMonth(result.getInteger("month"));
                    monthAnalytic.setCount(result.getInteger("totalCount"));
                    monthAnalyticRepository.save(monthAnalytic);
                } else {
                    break;
                }
            }
            return mongoTemplate.findAll(Document.class, "month_analytics");
    }

    public String getAvgScore() throws Exception {
        return "Average Score";
    }

    public String getHighestRatedMedia() throws Exception {
        return "Highest Rate";
    }

    public String getControversialMedia() throws Exception {
        return "Controversial";
    }

    public String getWorseningMedia() throws Exception {
        return "Worse Media";
    }

    public String getImprovingMedia() throws Exception {
        return "Better Media";
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
