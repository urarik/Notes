package com.example.analyzerneo4j.repository.custom;


import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Repository;

import javax.naming.event.ObjectChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ClassDiagramRepositoryImpl implements ClassDiagramRepository{
    private final Driver driver;

    //TODO N+1 problem
    public ClassDiagramRepositoryImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public Iterable<Node> findByIdAndDepth(Long cid, Long pid, Long depth) {
        Session session = driver.session();
        //labels, id, properties
        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n" +
                        "WITH project as project\n" +
                        "MATCH (project)-[*1..2]->(c)-[:relates|nested*1.."+depth+"]-(target)\n" +
                        "WHERE id(c)="+cid+" and (labels(c) in [['Class'], ['Interface']]) \n" +
                        "WITH target as list1, [c] as list2\n" +
                        "UNWIND list1 + list2 AS entity\n" +
                        "RETURN distinct entity").stream()
                .map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asNode)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> findEntityById(Long pid, Long eid) {
        Session session = driver.session();
        Node node = session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n" +
                "WITH project as project\n" +
                "MATCH (c)\n" +
                "WHERE id(c)="+eid+" and (labels(c) in [['Class'], ['Interface']]) \n" +
                "RETURN c").next().values().get(0).asNode();
        Map<String, Object> map = new HashMap<>(node.asMap());
        map.put("type", node.labels().iterator().next());
        return map;
    }

    @Override
    public Iterable<Object> findMemberByName(Long cid, Long pid) {
        Session session = driver.session();

        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n " +
                "WITH project as project\n " +
                "MATCH (project)-[*1..2]->(c: Class)-[:belongs_to_class]-(m:Member)-[:is_member_type_of]-(t)\n " +
                "WHERE id(c)="+cid+" " +
                "RETURN distinct m{.*, id:id(m), type: t.name}").stream()
                .map(Record::asMap)
                .map(record -> record.get("m"))
                .collect(Collectors.toList());

    }

    @Override
    public Iterable<Object> findMethodByName(Long cid, Long pid) {
        Session session = driver.session();

        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n" +
                        "WITH project as project\n" +
                        "MATCH (project)-[*1..2]->(c)-[:belongs_to]-(m:Method)-[:is_return_type_of]-(r),\n" +
                        "(m)-[:belongs_to_method]-(r1)\n" +
                        "WHERE id(c)="+cid+" and (labels(c) in [['Class'], ['Interface']])\n" +
                        "WITH m as m, r as r, collect(r1.name) as parameters\n" +
                        "RETURN m{.*, id:id(m), returnType: r.name, parameters: parameters}").stream()
                .map(Record::asMap)
                .map(record -> record.get("m"))
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
                        "MATCH p=(project)-[*1..2]->(c)-[:relates|nested*1.."+depth+"]-(t)\n" +
                        "WHERE id(c)="+cid+" and (labels(c) in [['Class'], ['Interface']]) \n" +
                        "UNWIND relationships(p) as rel\n" +
                        "WITH rel as rel\n" +
                        "WHERE type(rel)='relates' OR type(rel)='nested'\n" +
                        "return distinct rel;")
                .stream().map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asRelationship)
                .collect(Collectors.toList());
    }

}
