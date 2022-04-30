package com.example.analyzerneo4j.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Getter
@Setter
@RelationshipProperties
public class InterfaceRelationship {
    @GeneratedValue
    @RelationshipId
    private Long id;

    private final String type;
    private String name;

    @TargetNode
    private final Interface from;

    public InterfaceRelationship(String type, Interface from) {
        this.type = type;
        this.from = from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterfaceRelationship)) return false;

        InterfaceRelationship that = (InterfaceRelationship) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return from != null ? from.equals(that.from) : that.from == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        return result;
    }
}
