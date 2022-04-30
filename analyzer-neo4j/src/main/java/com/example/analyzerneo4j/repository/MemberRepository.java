package com.example.analyzerneo4j.repository;

import com.example.analyzerneo4j.entity.Member;
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;

public interface MemberRepository  extends ReactiveNeo4jRepository<Member, Long> {

    @Query(value = "MATCH (project: Project) WHERE $pid in project.pids\n " +
            "WITH project as project\n " +
            "MATCH (project)-[*1..2]->(c: Class)-[:belongs_to_class]-(m:Member)\n " +
            "WHERE c.name=$name " +
            "RETURN distinct m")
    Flux<Member> findMember(String name, Long pid);
}
