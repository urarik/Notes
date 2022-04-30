package com.example.analyzerneo4j.entity.tutorial;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import javax.swing.plaf.multi.MultiViewportUI;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.*;

// the @Node annotation is used to mark the class as a managed entity.
@Node("Movie")
@Getter
@Setter
@ToString
public class MovieEntity {
    @Id
    private String title;
    @Property("tagline")
    private String description;
    @Relationship(type = "ACTED_IN", direction = INCOMING)
    private Set<ActedIn> actors = new HashSet<>();
    @Relationship(type = "DIRECTED", direction = INCOMING)
    private Set<PersonEntity> directors = new HashSet<>();

    public MovieEntity() {}
    public MovieEntity(String title, String description) {
        this.title = title;
        this.description = description;
    }

}
