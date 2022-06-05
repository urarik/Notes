package com.urarik.notes_server.analysis.sd.table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name="MESSAGE")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long _id;

    @Column(nullable = false, updatable = false, name = "id")
    private Long id;

    @Column
    private Long fromCid;
    @Column
    private Long toCid;
    @Column
    private String methodName;
    @Column
    private String type;
    @Column
    private Long absHeight;
    @Column
    private String msg;

    public Message() {}
}
