package com.example.analyzerneo4j.entity.tutorial;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.List;

// https://community.neo4j.com/t/spring-data-neo4j-implement-relationship-entity-with-relationshipproperties-and-targetnode/38429
@RelationshipProperties
@NoArgsConstructor
@Getter
@Setter
public class ActedIn {
    @RelationshipId
    private Long id;

    private List<String> roles;
    @TargetNode
    private PersonEntity person;

    public ActedIn(List<String> roles, PersonEntity person) {
        this.roles = roles;
        this.person = person;

    }
}
