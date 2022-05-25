package com.example.analyzerneo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Class")
@ToString
@Getter
@Setter
public class Class {
    @GeneratedValue
    @Id
    private Long id;
    private String url;
    private String name;
    private Boolean isAbstract;
    private Boolean isStatic;

    //TODO 이름이랑 방향이 반대임
    @Relationship(type="belongs_to_package", direction = Relationship.Direction.INCOMING)
    private Package aPackage;

    @Relationship(type = "relates", direction = Relationship.Direction.INCOMING)
    private Set<ClassRelationship> classRelates = new HashSet<>();
    @Relationship(type = "relates", direction = Relationship.Direction.INCOMING)
    private Set<InterfaceRelationship> interfaceRelates = new HashSet<>();
    @Relationship(type = "nested", direction = Relationship.Direction.INCOMING)
    private Set<Class> nested = new HashSet<>();

    public Class(String url, String name, Boolean isAbstract, Boolean isStatic, Package aPackage) {
        this.url = url;
        this.name = name;
        this.isAbstract = isAbstract;
        this.isStatic = isStatic;
        this.aPackage = aPackage;

    }

}
