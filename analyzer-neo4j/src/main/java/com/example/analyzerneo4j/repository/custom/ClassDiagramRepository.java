package com.example.analyzerneo4j.repository.custom;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

public interface ClassDiagramRepository {
    Iterable<Node> findByIdAndDepth(Long cid, Long pid, Long depth);
    Iterable<Node> findMemberByName(Long cid, Long pid);
    Iterable<Node> findMethodByName(Long cid, Long pid);
    Iterable<Relationship> findClassRelationshipById(Long cid, Long pid, Long depth);
}
