package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.MonthAnalytic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthAnalyticRepository extends MongoRepository<MonthAnalytic, String> {
    MonthAnalytic findTopByOrderByYearDesc();
    List<MonthAnalytic> findAllByOrderByYear();
}