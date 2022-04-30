package com.example.analyzerneo4j.repository;

import com.example.analyzerneo4j.entity.Parameter;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface ParameterRepository  extends ReactiveNeo4jRepository<Parameter, Long> {
}
