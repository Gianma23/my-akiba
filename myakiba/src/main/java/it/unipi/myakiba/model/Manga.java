package it.unipi.myakiba.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "manga")
@Data
public class Manga {
}
