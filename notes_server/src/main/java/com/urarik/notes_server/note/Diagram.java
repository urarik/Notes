package com.urarik.notes_server.note;

import com.urarik.notes_server.analysis.sd.table.SDPlane;
import com.urarik.notes_server.analysis.table.CDPlane;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "DIAGRAM")
@Getter
@Setter
public class Diagram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @OneToOne
    private CDPlane cdPlane;
    @OneToOne
    private SDPlane sdPlane;

    @OneToOne(cascade = CascadeType.ALL)
    private Block belongsTo;


    public Diagram() {}
}
