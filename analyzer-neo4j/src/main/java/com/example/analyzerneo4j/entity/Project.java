package com.example.analyzerneo4j.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Project")
@Getter
@Setter
public class Project {
    @Id
    private String url;

    // TODO 지금 url은 github repository url
    // 만약 어느 레포의 하위 링크를 주면 새롭게 프로젝트가 생성됨
    // 하위 링크를 주면 이미 생성된 프로젝트라면 기존에 있는 분석 결과를 링킹하게끔?

    private Set<Long> pids;
    @Relationship(type = "has_a", direction = Relationship.Direction.OUTGOING)
    private Set<Package> packages = new HashSet<>();

    public Project(String url, Set<Long> pids) {
        this.url = url;
        this.pids = pids;
    }
}
