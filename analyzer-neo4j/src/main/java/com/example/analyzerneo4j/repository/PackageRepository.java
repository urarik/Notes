package com.example.analyzerneo4j.repository;

import com.example.analyzerneo4j.entity.Package;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface PackageRepository  extends ReactiveNeo4jRepository<Package, String> {
    @Query("MATCH (p:Package {path: $path}) WHERE $pid in p.projectId RETURN p")
    Mono<Package> findPackageByPath(String path);
}
