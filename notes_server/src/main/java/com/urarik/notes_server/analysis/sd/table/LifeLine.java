package com.urarik.notes_server.analysis.sd.table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@javax.persistence.Entity
@Table(name="LIFELINE")
@Getter
@Setter
public class LifeLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long _id;

    @Column(nullable = false, updatable = false, name = "id")
    private Long id;

    @Column
    Double absLeft;
    @Column
    Double absTop;
    @Column
    Double absW;
    @Column
    Double absH;
    @Column
    String name;
    @Column
    String url;

    //messages
    @OneToMany(cascade = CascadeType.ALL)
    Set<Message> messageSet;

    public LifeLine() {}
}
