package com.example.analyzerneo4j.repository;

import com.example.analyzerneo4j.entity.Method;
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;

public interface MethodRepository  extends ReactiveNeo4jRepository<Method, Long> {
    @Query(value = "MATCH (project: Project) WHERE $pid in project.pids\n " +
            "WITH project as project\n " +
            "MATCH (project)-[*1..2]->(c: Class {name: '$name'})-[:belongs_to]-(m:Method)\n " +
            "RETURN distinct m")
    Flux<Node> findMethod(String name, Long pid);
}
