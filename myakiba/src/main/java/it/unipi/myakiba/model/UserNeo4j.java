package it.unipi.myakiba.model;

import it.unipi.myakiba.repository.UserNeo4jRepository;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("User")
@Data
public class UserNeo4j {

    @Id
    private String id;

    @Property("username")
    private String username;

    @Property("email")
    private String email;

/*    @Relationship(type = "FOLLOW", direction = Relationship.Direction.OUTGOING)
    private List<UserNeo4j> following;

    @Relationship(type = "FOLLOW", direction = Relationship.Direction.INCOMING)
    private List<UserNeo4j> followers;

    @Relationship(type = "WATCHED", direction = Relationship.Direction.OUTGOING)
    private List<AnimeNeo4j> watched;

    @Relationship(type = "WATCHING", direction = Relationship.Direction.OUTGOING)
    private List<AnimeNeo4j> watching;

    @Relationship(type = "WANT_TO_WATCH", direction = Relationship.Direction.OUTGOING)
    private List<AnimeNeo4j> wantToWatch;*/
}
