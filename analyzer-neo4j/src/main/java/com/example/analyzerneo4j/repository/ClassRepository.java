package com.example.analyzerneo4j.repository;

import com.example.analyzerneo4j.entity.Class;
import com.example.analyzerneo4j.entity.ClassRelationship;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface ClassRepository extends ReactiveNeo4jRepository<Class, Long> {
    @Query("MATCH (p: Project)-[*1..3]-(c: Class {name: $name})\n" +
            "WHERE $pid in p.pids\n" +
            "RETURN c\n " +
            "LIMIT 1")
    Mono<Class> findClassByName(Long pid, String name);

    @Query("MATCH (project: Project) WHERE $pid in project.pids " +
            "MATCH (c: Class {name: $name})-[:relates*1..$depth]-(target: Class) " +
            "RETURN target")
    Flux<Class> findClassByName(@Param("name") String name, Long pid, @Param("depth") Long depth);
}
