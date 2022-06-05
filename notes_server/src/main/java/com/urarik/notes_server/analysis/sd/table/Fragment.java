package com.urarik.notes_server.analysis.sd.table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name="FRAGMENT")
@Getter
@Setter
public class Fragment {
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
    String type;
    @Column
    String content;
    @Column
    Long fromId;
    @Column
    Long toId;

    public Fragment() {}
}
