package com.urarik.notes_server.project.dto;

import java.io.Serializable;
import java.util.List;

public class Roles implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> members;
    private List<String> admins;
    private Long id;

    public Roles() {}

    public Roles(List<String> members, List<String> admins, Long id) {
        this.members = members;
        this.admins = admins;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }
}
