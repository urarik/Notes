package com.urarik.notes_server.analysis.table;

import javax.persistence.*;
import java.util.Set;

@javax.persistence.Entity
@Table(name="PLANE")
public class CDPlane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column
    Long pid;
    @Column
    Double viewLeft;
    @Column
    Double viewTop;
    @Column
    Double viewW;
    @Column
    Double viewH;
    @Column
    Double containerW;
    @Column
    Double containerH;
    @Column
    String name;
    @Column
    Double fontSize;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "plane_id")
    Set<Entity> entitySet;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "plane_id")
    Set<Relationship> relationshipSet;

    public CDPlane() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Double getViewLeft() {
        return viewLeft;
    }

    public void setViewLeft(Double viewLeft) {
        this.viewLeft = viewLeft;
    }

    public Double getViewTop() {
        return viewTop;
    }

    public void setViewTop(Double viewTop) {
        this.viewTop = viewTop;
    }

    public Double getViewW() {
        return viewW;
    }

    public void setViewW(Double viewW) {
        this.viewW = viewW;
    }

    public Double getViewH() {
        return viewH;
    }

    public void setViewH(Double viewH) {
        this.viewH = viewH;
    }

    public Double getContainerW() {
        return containerW;
    }

    public void setContainerW(Double containerW) {
        this.containerW = containerW;
    }

    public Double getContainerH() {
        return containerH;
    }

    public void setContainerH(Double containerH) {
        this.containerH = containerH;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFontSize() {
        return fontSize;
    }

    public void setFontSize(Double fontSize) {
        this.fontSize = fontSize;
    }

    public Set<Entity> getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Set<Entity> entitySet) {
        this.entitySet = entitySet;
    }

    public Set<Relationship> getRelationshipSet() {
        return relationshipSet;
    }

    public void setRelationshipSet(Set<Relationship> relationshipSet) {
        this.relationshipSet = relationshipSet;
    }
}
