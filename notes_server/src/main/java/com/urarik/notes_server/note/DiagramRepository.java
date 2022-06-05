package com.urarik.notes_server.note;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DiagramRepository extends CrudRepository<Diagram, Long> {
    @Query("select d from Diagram d where d.belongsTo.bid = ?1")
    Optional<Diagram> findByBid(Long bid);
}
