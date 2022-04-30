package com.urarik.notes_server.note;

import com.urarik.notes_server.project.ProjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends CrudRepository<Block, Long> {

    @Query("select b from Block b where b.belongsTo.nid = ?1 order by b.sequence")
    List<BlockView> findByNid(Long nid);

    interface BlockView {
        Long getBid();
        Long getSequence();
        String getType();
        NoteRepository.NidView getBelongsTo();
    }

    @Query("select max(b.sequence) from Block b where b.belongsTo.nid = ?1")
    Optional<Long> findLastSequence(Long nid);
}
