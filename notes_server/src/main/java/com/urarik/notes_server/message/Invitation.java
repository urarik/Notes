package com.urarik.notes_server.message;

import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.project.ProjectRepository;
import com.urarik.notes_server.security.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "INVITATION")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long iid;

    @Column(nullable = true)
    private Date date;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    @ManyToOne
    private Project project;

    public Invitation() {}
    public Invitation(User sender, User receiver, Project project) {
        this.sender = sender;
        this.receiver = receiver;
        this.project = project;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
