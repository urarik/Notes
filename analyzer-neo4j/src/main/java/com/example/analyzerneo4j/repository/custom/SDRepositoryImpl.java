package com.example.analyzerneo4j.repository.custom;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SDRepositoryImpl implements SDRepository {
    private final Driver driver;

    public SDRepositoryImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public Iterable<Map<String, Object>> findById(Long pid, Long mid) {
        Session session = driver.session();
        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n" +
                        "WITH project as project\n" +
                        "MATCH p=(project)-[*3]-(m1: Method)-[:invokes*0..1]->(m: Method)\n" +
                        "WHERE id(m1) = "+mid+"\n" +
                        "AND relationships(p)[3] is not NULL\n" +
                        "WITH relationships(p)[3] AS i, m\n" +
                        "OPTIONAL MATCH (m: Method)-[:is_return_type_of]-(e)\n" +
                        "MATCH (m)-[:belongs_to]-(c)\n" +
                        "return {\n" +
                        "    method: {\n" +
                        "        id: id(m),\n" +
                        "        isStatic: m.isStatic,\n" +
                        "        name: m.name,\n" +
                        "        returnType: e.name\n" +
                        "    },\n" +
                        "    invokes: {\n" +
                        "        id: id(i),\n" +
                        "        arguments: i.arguments,\n" +
                        "        order: i.order,\n" +
                        "        states: i.states\n" +
                        "    },\n" +
                        "    entity: {\n" +
                        "        name: c.name,\n" +
                        "        url: c.url,\n" +
                        "        id: id(c)\n" +
                        "    }\n" +
                        "}\n").stream()
                .map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asMap)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<List<Value>> findBeforeById(Long pid, Long mid) {
        Session session = driver.session();

        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n" +
                "WITH project as project\n" +
                "MATCH p=(project)-[*3]->(m1)<-[:invokes]-(method)\n" +
                "WHERE id(m1) = "+mid+"\n" +
                "return method, relationships(p)[3] as invokes").stream()
                .map(Record::values)
                .collect(Collectors.toList());

    }
}
