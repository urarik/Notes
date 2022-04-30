package com.urarik.notes_server.project;

import com.urarik.notes_server.security.User;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="PROJECT")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long pid;

    @ManyToOne
    private User owner;

    @ManyToMany
    private Set<User> admin;

    @ManyToMany
    private Set<User> member;

    @Column(nullable = false)
    private String title;

    public Project() {}
    public Project(Long pid, User owner, Set<User> admin, Set<User> member, String title) {
        this.pid = pid;
        this.owner = owner;
        this.admin = admin;
        this.member = member;
        this.title = title;
    }
    public Project(User owner, Set<User> admin, Set<User> member, String title) {
        this.owner = owner;
        this.admin = admin;
        this.member = member;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Set<User> getAdmin() {
        return admin;
    }

    public void setAdmin(Set<User> admin) {
        this.admin = admin;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Set<User> getMember() {
        return member;
    }

    public void setMember(Set<User> member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "Project{" +
                "pid=" + pid +
                ", owner=" + owner +
                ", admin=" + admin +
                ", member=" + member +
                ", title='" + title + '\'' +
                '}';
    }

}
