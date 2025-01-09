package it.unipi.myakiba.model;

import it.unipi.myakiba.enumerator.MediaStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

@Node("Anime")
@Data
public class AnimeNeo4j {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("status")
    private MediaStatus status;

    @Property("episodes")
    private int episodes;

    @Property("genres")
    private List<String> genres;
}