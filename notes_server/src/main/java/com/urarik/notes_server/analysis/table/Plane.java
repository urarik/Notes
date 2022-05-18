package com.urarik.notes_server.analysis.table;

import javax.persistence.*;
import java.util.List;

@javax.persistence.Entity
@Table(name="PLANE")
public class Plane {
    @Id
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

    @OneToMany(fetch = FetchType.EAGER)
    List<Entity> entityList;
    @OneToMany(fetch = FetchType.EAGER)
    List<Relationship> relationshipList;

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

    public List<Entity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public List<Relationship> getRelationshipList() {
        return relationshipList;
    }

    public void setRelationshipList(List<Relationship> relationshipList) {
        this.relationshipList = relationshipList;
    }
}
