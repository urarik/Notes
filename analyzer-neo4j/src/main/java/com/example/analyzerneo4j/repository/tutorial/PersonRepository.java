package com.example.analyzerneo4j.repository.tutorial;

import com.example.analyzerneo4j.entity.tutorial.MovieEntity;
import com.example.analyzerneo4j.entity.tutorial.PersonEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface PersonRepository extends ReactiveNeo4jRepository<PersonEntity, String> {
}
