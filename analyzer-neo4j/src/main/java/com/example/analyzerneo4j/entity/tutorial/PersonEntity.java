package com.example.analyzerneo4j.entity.tutorial;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Person")
@Getter
@Setter
@NoArgsConstructor
public class PersonEntity {
    @Id
    private String name;
    private Integer born;
    public PersonEntity(Integer born, String name) {
        this.born = born;
        this.name = name;
    }
}