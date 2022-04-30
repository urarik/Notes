package com.urarik.notes_server.note;

import com.urarik.notes_server.project.Project;

import javax.persistence.*;

@Entity
@Table(name = "BLOCK")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long bid;

    @ManyToOne
    private Note belongsTo;

    @Column(nullable = false, unique = false)
    private Long sequence;

    @Column(nullable = false)
    private String type;

    public Block() {}

    public Block(Long bid, Note belongsTo, Long sequence, String type) {
        this.bid = bid;
        this.belongsTo = belongsTo;
        this.sequence = sequence;
        this.type = type;
    }
    public Block(Note belongsTo, Long sequence, String type) {
        this.belongsTo = belongsTo;
        this.sequence = sequence;
        this.type = type;
    }

    public Note getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(Note belongsTo) {
        this.belongsTo = belongsTo;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }
}
