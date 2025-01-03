package it.unipi.myakiba.model;

import it.unipi.myakiba.DTO.UserIdNameDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "clique_analytics")
@Data
public class CliqueAnalytic {
    @Id
    private String cliqueId;
    private int cliqueSize;
    List<UserIdNameDto> userDetails;
}