package com.example.analyzerneo4j.repository.custom;

import org.neo4j.driver.types.Node;

public interface ListRepository {
    Iterable<Node> findPackage(Long pid, Long order);
    Iterable<Node> findClassAndInterface(Long pid, Long packageId);
}
