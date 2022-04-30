package com.urarik.notes_server.note;

import com.urarik.notes_server.note.Note;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name="TEXT")
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long tid;

    @Column(nullable = false)
    private String content;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Block belongsTo;

    public Text() {}

    public Text(Long tid, String content, Block belongsTo) {
        this.tid = tid;
        this.content = content;
        this.belongsTo = belongsTo;
    }
    public Text(String content, Block belongsTo) {
        this.content = content;
        this.belongsTo = belongsTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Block getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(Block belongsTo) {
        this.belongsTo = belongsTo;
    }
}
