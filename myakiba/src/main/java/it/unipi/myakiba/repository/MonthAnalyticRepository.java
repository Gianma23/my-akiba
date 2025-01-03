package it.unipi.myakiba.repository;

import it.unipi.myakiba.DTO.MonthAnalyticDto;
import it.unipi.myakiba.model.MonthAnalytic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthAnalyticRepository extends MongoRepository<MonthAnalytic, String> {
    @Query(value = "{}", sort = "{'_id': -1}", fields = "{'_id': 1}")
    MonthAnalyticDto findTopByOrderByIdDesc();
}