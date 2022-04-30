package com.example.analyzerneo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Set;

@Node("Package")
@EqualsAndHashCode
@Getter
@Setter
public class Package {
    @Id
    private String path;

    public Package(String path) {
        this.path = path;
    }
}
