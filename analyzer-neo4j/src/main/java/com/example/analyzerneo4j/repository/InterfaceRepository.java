package com.example.analyzerneo4j.repository;

import com.example.analyzerneo4j.entity.Class;
import com.example.analyzerneo4j.entity.Interface;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface InterfaceRepository  extends ReactiveNeo4jRepository<Interface, Long> {
    @Query("MATCH (p: Project)-[*1..3]-(c: Interface {name: $name})\n" +
            "WHERE $pid in p.pids\n" +
            "RETURN c\n " +
            "LIMIT 1")
    Mono<Interface> findInterfaceByName(Long pid, String name);
    @Query("MATCH (project: Project) WHERE $pid in project.pids " +
            "MATCH (c: Interface {name: $name})-[:relates*1]-(target) " +
            "RETURN target")
    Flux<Class> findInterfaceByName(String name, Long pid, Long depth);
}
