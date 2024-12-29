package it.unipi.myakiba.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "anime")
@Data
public class Anime {
}
