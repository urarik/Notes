package com.urarik.notes_server.note;

import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.security.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="NOTE")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long nid;

    @ManyToOne
    private Project belongsTo;

    @Column(nullable = true, unique = false)
    private Long sequence;

    @Column(nullable = false, unique = false)
    private Boolean isMain;

    @Column(nullable = false)
    private String title;

    @ManyToMany
    @Column(nullable = true)
    private Set<User> admins;

    @Transient
    private Long pid; // for NoteController::createNote

    public Note(Long nid, Project belongsTo, Long sequence, Boolean isMain, String title, Set<User> admins) {
        this.nid = nid;
        this.belongsTo = belongsTo;
        this.sequence = sequence;
        this.isMain = isMain;
        this.title = title;
        this.admins = admins;
        this.pid = pid;
    }
    public Note(Project belongsTo, Long sequence, Boolean isMain, String title, Set<User> admins) {
        this.belongsTo = belongsTo;
        this.sequence = sequence;
        this.isMain = isMain;
        this.title = title;
        this.admins = admins;
        this.pid = pid;
    }

    public Note() {
    }

    public Project getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(Project belongsTo) {
        this.belongsTo = belongsTo;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean main) {
        isMain = main;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
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

    @Override
    public String toString() {
        return "Note{" +
                "nid=" + nid +
                ", belongsTo=" + belongsTo +
                ", sequence=" + sequence +
                ", isMain=" + isMain +
                ", title='" + title + '\'' +
                ", admins=" + admins +
                ", pid=" + pid +
                '}';
    }
}
