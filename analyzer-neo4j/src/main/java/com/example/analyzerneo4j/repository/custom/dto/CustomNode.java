package com.example.analyzerneo4j.repository.custom.dto;

import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;

import java.util.Map;
import java.util.function.Function;

public class CustomNode implements Node {
    Iterable<String> labels;
    long id;
    Map<String, Object> map;

    public CustomNode(Iterable<String> labels, long id, Map<String, Object> map) {
        this.labels = labels;
        this.id = id;
        this.map = map;
    }

    @Override
    public Iterable<String> labels() {
        return labels;
    }
    @Override
    public long id() {
        return id;
    }
    @Override
    public Map<String, Object> asMap() {
        return map;
    }


    @Override
    public boolean hasLabel(String s) {
        return false;
    }

    @Override
    public Iterable<String> keys() {
        return null;
    }

    @Override
    public boolean containsKey(String s) {
        return false;
    }

    @Override
    public Value get(String s) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterable<Value> values() {
        return null;
    }

    @Override
    public <T> Iterable<T> values(Function<Value, T> function) {
        return null;
    }

    @Override
    public <T> Map<String, T> asMap(Function<Value, T> function) {
        return null;
    }
}
