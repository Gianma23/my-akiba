package it.unipi.myakiba.repository;

import it.unipi.myakiba.model.CliqueAnalytic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CliqueAnalyticRepository extends MongoRepository<CliqueAnalytic,String> {
}
