package com.example.analyzerneo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Parameter")
@EqualsAndHashCode
@Getter
@Setter
public class Parameter {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type="is_parameter_type_of", direction = Relationship.Direction.OUTGOING)
    private Class classType;
    @Relationship(type="is_parameter_type_of", direction = Relationship.Direction.OUTGOING)
    private Interface interfaceType;

    @Relationship(type="belongs_to_method", direction = Relationship.Direction.INCOMING)
    private Method method;

    public Parameter(String name, Method method) {
        this.name = name;
        this.method = method;
    }

}
