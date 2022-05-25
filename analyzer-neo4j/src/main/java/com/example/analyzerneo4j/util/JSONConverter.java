package com.example.analyzerneo4j.util;

import org.json.JSONObject;
import org.neo4j.driver.types.Relationship;

import java.util.Map;

public class JSONConverter {
    public static JSONObject nodeToJSON(org.neo4j.driver.types.Node node) {
        return new JSONObject()
                .put("id", node.id())
                .put("label", node.labels())
                .put("properties", node.asMap());
    }

    public static JSONObject mapToJSON(Map<Object, Object> map) {
        JSONObject jsonObject = new JSONObject();
        map.forEach((key, value) -> jsonObject.put((String) key, value));
        return jsonObject;
    }
    public static JSONObject stringMapToJSON(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        map.forEach(jsonObject::put);
        return jsonObject;
    }

    public static JSONObject relationshipToJSON(Relationship relationship) {
        return new JSONObject()
                .put("start", relationship.startNodeId())
                .put("end", relationship.endNodeId())
                .put("type", relationship.type())
                .put("id", relationship.id())
                .put("properties", relationship.asMap());
    }
}
