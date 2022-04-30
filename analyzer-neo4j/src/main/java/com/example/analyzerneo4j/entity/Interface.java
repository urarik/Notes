package com.example.analyzerneo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Interface")
@Getter
@Setter
public class Interface {
    @GeneratedValue
    @Id
    private Long id;
    private String url;
    private String name;

    @Relationship(type="belongs_to_package", direction = Relationship.Direction.INCOMING)
    private Package aPackage;

    public Interface(String url, String name, Package aPackage) {
        this.url = url;
        this.name = name;
        this.aPackage = aPackage;
    }

    @Relationship(type = "relates", direction = Relationship.Direction.INCOMING)
    private Set<ClassRelationship> classRelates = new HashSet<>();
    @Relationship(type = "relates", direction = Relationship.Direction.INCOMING)
    private Set<InterfaceRelationship> interfaceRelates = new HashSet<>();
    @Relationship(type = "nested", direction = Relationship.Direction.INCOMING)
    private Set<Class> nested = new HashSet<>();

}
