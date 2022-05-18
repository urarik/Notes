package com.example.analyzerneo4j.repository.custom;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.Map;

public interface ClassDiagramRepository {
    Iterable<Node> findByIdAndDepth(Long cid, Long pid, Long depth);
    Iterable<Object> findMemberByName(Long cid, Long pid);
    Iterable<Object> findMethodByName(Long cid, Long pid);
    Iterable<Relationship> findClassRelationshipById(Long cid, Long pid, Long depth);
    Map<String, Object> findEntityById(Long pid, Long eid);
}
