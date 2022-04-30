package com.example.analyzerneo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Member")
@EqualsAndHashCode
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    private String visibility;
    private String name;
    private Boolean isStatic;

    @Relationship(type="belongs_to_class", direction = Relationship.Direction.INCOMING)
    private Class aClass;

    @Relationship(type="is_member_type_of", direction = Relationship.Direction.OUTGOING)
    private Class classType;

    @Relationship(type="is_member_type_of", direction = Relationship.Direction.OUTGOING)
    private Interface interfaceType;

    public Member(String visibility, String name, Boolean isStatic) {
        this.visibility = visibility;
        this.name = name;
        this.isStatic = isStatic;
    }
}
