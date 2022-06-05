package com.urarik.notes_server.analysis.sd.table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@javax.persistence.Entity
@Table(name="SDPLANE")
@Getter
@Setter
public class SDPlane {
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
    @Column
    Double msgOffset;
    @Column
    Double lineLength;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "plane_id")
    Set<LifeLine> lifeLineSet;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "plane_id")
    Set<Fragment> fragmentSet;


    public SDPlane() { }
}
