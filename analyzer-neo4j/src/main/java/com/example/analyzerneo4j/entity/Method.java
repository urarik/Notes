package com.example.analyzerneo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Method")
@Getter
@Setter
public class Method {
    @Id
    @GeneratedValue
    private Long id;

    private String visibility;
    private String name;
    private Boolean isStatic;

    @Relationship(type="belongs_to", direction = Relationship.Direction.OUTGOING)
    private Class aClass;
    @Relationship(type="belongs_to", direction = Relationship.Direction.OUTGOING)
    private Interface anInterface;

    @Relationship(type="is_return_type_of", direction = Relationship.Direction.OUTGOING)
    private Class classReturnType;
    @Relationship(type="is_return_type_of", direction = Relationship.Direction.OUTGOING)
    private Interface interfaceReturnType;

    @Relationship(type = "invokes", direction = Relationship.Direction.OUTGOING)
    private List<MethodRelationship> invokes = new ArrayList<>();

    public Method(String visibility, String name, Boolean isStatic) {
        this.visibility = visibility;
        this.name = name;
        this.isStatic = isStatic;
    }

}
