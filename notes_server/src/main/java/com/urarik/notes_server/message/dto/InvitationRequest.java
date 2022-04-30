package com.urarik.notes_server.message.dto;

import java.io.Serializable;
import java.util.List;

public class InvitationRequest implements Serializable {
    List<String> userNames;
    Long id;

    public InvitationRequest() {}
    public InvitationRequest(List<String> userNames, Long id) {
        this.userNames = userNames;
        this.id = id;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
