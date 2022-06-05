package com.urarik.notes_server.analysis.sd;

import com.urarik.notes_server.analysis.dto.PlaneWithName;
import com.urarik.notes_server.analysis.sd.table.SDPlane;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SDPlaneRepository extends CrudRepository<SDPlane, Long> {
    @Query(value = "select plane from SDPlane plane where plane.pid = ?1")
    List<PlaneWithName> findPlanesByPid(Long pid);
}
