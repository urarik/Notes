package com.example.analyzerneo4j.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Getter
@Setter
@RelationshipProperties
public class MethodRelationship {
    @RelationshipId
    @GeneratedValue
    private Long id;

    private Long order;

    private List<State> in;
    private List<String> arguments;

    @Relationship(type = "is_parent_of", direction = Relationship.Direction.OUTGOING)
    private Long parentOrder;

    @TargetNode
    private Method to;

    public MethodRelationship(Long order, List<State> in, List<String> arguments, Method to) {
        this.order = order;
        this.in = in;
        this.arguments = arguments;
        this.to = to;
    }
}
