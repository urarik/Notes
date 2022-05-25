package com.example.analyzerneo4j.service;

import com.example.analyzerneo4j.repository.custom.SDRepository;
import com.example.analyzerneo4j.repository.custom.SDRepositoryImpl;
import org.json.JSONObject;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;
import static com.example.analyzerneo4j.util.JSONConverter.*;

@Component
public class SDService {
    private final SDRepository sdRepository;

    public SDService(SDRepositoryImpl sdRepositoryImpl) {
        this.sdRepository = sdRepositoryImpl;
    }

    public List<Map<String, Object>> getInvokes(Long pid, Long mid) {
        List<Map<String, Object>> ret = new ArrayList<>();

        StreamSupport.stream(sdRepository.findById(pid, mid).spliterator(), true)
                .forEach(map -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("method", stringMapToJSON((Map<String, Object>) map.get("method")).toMap());
                    row.put("invokes", stringMapToJSON((Map<String, Object>) map.get("invokes")).toMap());
                    row.put("entity", stringMapToJSON((Map<String, Object>) map.get("entity")).toMap());
                    ret.add(row);
                });
        return ret;
    }

    public List<Map<String, Object>> getBefore(Long pid, Long mid) {
        List<Map<String, Object>> ret = new ArrayList<>();

        StreamSupport.stream(sdRepository.findBeforeById(pid, mid).spliterator(), true)
                .forEach(list -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("method", nodeToJSON((list.get(0)).asNode()).toMap());
                    row.put("invokes", relationshipToJSON((list.get(1)).asRelationship()).toMap());
                    ret.add(row);
                });
        return ret;
    }
}
