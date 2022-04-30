package com.example.analyzerneo4j.repository.custom;

import com.example.analyzerneo4j.repository.custom.dto.CustomNode;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ListRepositoryImpl implements ListRepository{
    private final Driver driver;

    public ListRepositoryImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public Iterable<Node> findPackage(Long pid, Long order) {
        Session session = driver.session();
        //labels, id, properties
        return session.run("MATCH (project: Project) WHERE "+pid+" in project.pids\n" +
                        "WITH project as project\n" +
                        "MATCH (project)-[:has_a]->(package: Package)\n" +
                        "RETURN package\n" +
                        "SKIP 20 * "+order+"\n" +
                        "LIMIT 20").stream()
                .map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asNode)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Node> findClassAndInterface(Long pid, Long packageId) {
        Session session = driver.session();
        //labels, id, properties
        return session.run("MATCH (project: Project)-[:has_a]->(package: Package) \n" +
                        "WHERE "+pid+" in project.pids \n" +
                        "AND id(package)="+packageId+"\n" +
                        "WITH package as package\n" +
                        "MATCH (package)-[:belongs_to_package]->(entity)\n" +
                        "RETURN entity").stream()
                .map(Record::values)
                .map(values -> values.get(0))
                .map(Value::asNode)
                .map(node -> {
                    Map<String, Object> map = new HashMap<>(node.asMap());
                    map.remove("isAbstract");
                    map.remove("isStatic");
                    return new CustomNode(node.labels(), node.id(), map);
                })
                .collect(Collectors.toList());
    }
}
