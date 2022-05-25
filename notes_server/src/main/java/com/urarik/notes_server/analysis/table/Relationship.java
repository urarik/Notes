package com.urarik.notes_server.analysis.table;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name="RELATIONSHIP")
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long _id;

    @Column(nullable = false, updatable = false, name = "id")
    private Long id;

    @Column
    Long fromId;
    @Column
    Long toId;
    @Column
    Long theOrder;
    @Column
    Long size;
    @Column
    String type;
    @Column
    Double height;
    @Column
    Double edgeWidth;

    public Relationship() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public Long getTheOrder() {
        return theOrder;
    }

    public void setTheOrder(Long theOrder) {
        this.theOrder = theOrder;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getEdgeWidth() {
        return edgeWidth;
    }

    public void setEdgeWidth(Double edgeWidth) {
        this.edgeWidth = edgeWidth;
    }
}
