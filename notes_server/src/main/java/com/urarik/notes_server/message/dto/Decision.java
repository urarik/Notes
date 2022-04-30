package com.urarik.notes_server.message.dto;

import java.io.Serializable;

public class Decision implements Serializable {
    private Long iid;
    private Boolean accept;

    public Decision() {}
    public Decision(Long iid, Boolean accept) {
        this.iid = iid;
        this.accept = accept;
    }

    public Long getIid() {
        return iid;
    }

    public void setIid(Long iid) {
        this.iid = iid;
    }

    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }
}
