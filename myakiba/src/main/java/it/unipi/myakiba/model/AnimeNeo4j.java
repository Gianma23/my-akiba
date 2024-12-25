package it.unipi.myakiba.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Anime")
@Data
public class AnimeNeo4j {

    @Id
    @GeneratedValue
    private String id;

    @Property("username")
    private String username;

    @Property("email")
    private String email;
}
