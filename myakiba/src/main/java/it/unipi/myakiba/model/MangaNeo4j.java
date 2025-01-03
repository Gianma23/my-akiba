package it.unipi.myakiba.model;

import it.unipi.myakiba.enumerator.MediaProgress;
import it.unipi.myakiba.enumerator.MediaStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Manga")
@Data
public class MangaNeo4j {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("status")
    private MediaStatus status;

    @Property("chapters")
    private int chapters;

    @Property("genres")
    private List<String> genres;
}