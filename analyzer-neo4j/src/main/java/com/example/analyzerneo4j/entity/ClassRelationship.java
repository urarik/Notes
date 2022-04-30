package com.example.analyzerneo4j.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

@Getter
@Setter
@RelationshipProperties
public class ClassRelationship {
    @RelationshipId
    @GeneratedValue
    private Long id;

    private final String type;
    private String name;

    @TargetNode
    private final Class from;

    public ClassRelationship(String type, Class from) {
        this.type = type;
        this.from = from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassRelationship)) return false;

        ClassRelationship that = (ClassRelationship) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return from != null ? from.equals(that.from) : that.from == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        return result;
    }
}
