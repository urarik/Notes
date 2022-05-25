package com.urarik.notes_server.analysis;

import com.urarik.notes_server.analysis.dto.PlaneWithName;
import com.urarik.notes_server.analysis.table.Plane;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PlaneRepository extends CrudRepository<Plane, Long> {

    @NotNull Optional<Plane> findById(@NotNull Long id);

    @Query(value = "select plane from Plane plane where plane.pid = ?1")
    List<PlaneWithName> findPlanesByPid(Long pid);
}
