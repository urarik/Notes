package com.example.analyzerneo4j.repository.custom;


import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Repository
public class ClassDiagramRepositoryImpl implements ClassDiagramRepository{
    private final Driver driver;

    public ClassDiagramRepositoryImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public Iterable<Node> findByIdAndDepth(Long cid, Long pid, Long depth) {
        Session session = driver.session();
        //labels, id, properties
        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids " +
                "WITH project as project " +
                "MATCH (project)-[*1..2]->(c: Class)-[:relates*1.."+depth+"]-(target) " +
                "WHERE id(c)="+cid+" " +
                "RETURN distinct target").stream()
                .map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asNode)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Node> findMemberByName(Long cid, Long pid) {
        Session session = driver.session();

        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n " +
                "WITH project as project\n " +
                "MATCH (project)-[*1..2]->(c: Class)-[:belongs_to_class]-(m:Member)\n " +
                "WHERE id(c)="+cid+" " +
                "RETURN distinct m").stream()
                .map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asNode)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Node> findMethodByName(Long cid, Long pid) {
        Session session = driver.session();

        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n " +
                        "WITH project as project\n " +
                        "MATCH (project)-[*1..2]->(c: Class)-[:belongs_to]-(m:Method)\n " +
                        "WHERE id(c)="+cid+" " +
                        "RETURN distinct m").stream()
                .map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asNode)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Relationship> findClassRelationshipById(Long cid, Long pid, Long depth) {
        Session session = driver.session();


        // path를 p에 저장
        // relationships(p)는 List<List<Relationship>>이므로
        // UNWIND로 flatten한 후 relates만 가져오자
        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n" +
                        "WITH project as project\n" +
                        "MATCH p=(project)-[*1..2]->(c: Class)-[:relates*1.."+depth+"]-(t)\n" +
                        "WHERE id(c)="+cid+"\n" +
                        "UNWIND relationships(p) as rel\n" +
                        "WITH rel as rel\n" +
                        "WHERE type(rel)='relates'\n" +
                        "return distinct rel;")
                .stream().map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asRelationship)
                .collect(Collectors.toList());
    }

}
