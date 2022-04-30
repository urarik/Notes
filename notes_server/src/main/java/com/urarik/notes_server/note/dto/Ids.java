package com.urarik.notes_server.note.dto;

import java.io.Serializable;

public class Ids implements Serializable {
    private Long pid;
    private Long nid;
    private Long bid;

    public Ids() {}
    public Ids(Long pid, Long nid) {
        this.pid = pid;
        this.nid = nid;
    }
    public Ids(Long bid) {
        this.bid = bid;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getNid() {
        return nid;
    }

    public void setNid(Long nid) {
        this.nid = nid;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }
}
