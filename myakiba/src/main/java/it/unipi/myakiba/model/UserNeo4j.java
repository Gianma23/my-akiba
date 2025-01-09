package it.unipi.myakiba.model;

import it.unipi.myakiba.enumerator.PrivacyStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("User")
@Data
public class UserNeo4j {

    @Id
    private String id;

    @Property("username")
    private String username;

    @Property("privacyStatus")
    private PrivacyStatus privacyStatus;
}
