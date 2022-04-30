package com.urarik.notes_server.note;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TextRepository extends CrudRepository<Text, Long> {

    @Query("select t from Text t where t.belongsTo.bid = ?1")
    Optional<TextView> findByBid(Long bid);

    @Query("select t.tid from Text t where t.belongsTo.bid = ?1")
    Optional<Long> findTidByBid(Long bid);

    interface TextView {
        String getContent();
    }
}
