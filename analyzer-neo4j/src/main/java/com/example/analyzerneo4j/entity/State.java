package com.example.analyzerneo4j.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class State {
    String cmd;
    String type;
    String content;
    Long id;
    Long parentId;


    public State(String cmd, String type, String content, Long id, Long parentId) {
        this.cmd = cmd;
        this.type = type;
        this.content = content;
        this.id = id;
        this.parentId = parentId;
    }

    public State(String cmd, String type, String content, Long id) {
        this.cmd = cmd;
        this.type = type;
        this.content = content;
        this.id = id;
    }

    public State(String cmd) {
        this.cmd = cmd;
    }
}
