package com.example.analyzerneo4j.repository.custom;

import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;

import java.util.List;
import java.util.Map;

public interface SDRepository {
    Iterable<Map<String, Object>> findById(Long pid, Long mid);
    Iterable<List<Value>> findBeforeById(Long pid, Long mid);
}
