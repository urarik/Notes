package com.example.analyzerneo4j.repository;

import com.example.analyzerneo4j.entity.Project;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface ProjectRepository extends ReactiveNeo4jRepository<Project, String> {
}
