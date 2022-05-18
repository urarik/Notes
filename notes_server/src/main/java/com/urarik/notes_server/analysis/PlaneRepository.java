package com.urarik.notes_server.analysis;

import com.urarik.notes_server.analysis.table.Plane;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlaneRepository extends CrudRepository<Plane, Long> {

    @NotNull Optional<Plane> findById(@NotNull Long id);
}
