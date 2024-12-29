package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.MonthAnalytic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MonthAnalyticRepository extends MongoRepository<MonthAnalytic, String> {
    @Query(value = "{}", sort = "{'_id': -1}", fields = "{'_id': 1}")
    MonthAnalytic findTopByOrderByIdDesc();
}