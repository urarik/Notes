package com.urarik.notes_server.note;

import com.urarik.notes_server.project.ProjectRepository;
import com.urarik.notes_server.view.UserNameView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends CrudRepository<Note, Long> {

    @Query(value = "select n from Note n where n.isMain=true and n.belongsTo.pid = ?1")
    Optional<NoteView> findMainNoteWithView(Long pid);

    @Query(value = "select n from Note n where n.isMain=true and n.belongsTo.pid = ?1")
    Optional<Note> findMainNote(Long nid);

    @Query(value = "select n from Note n where n.nid = ?1")
    Optional<NoteView> findNoteViewByNid(Long nid);

    @Query(value = "select n from Note n where n.nid = ?1")
    Optional<Note> findNoteByNid(Long nid);

    // Paging?
    @Query(value = "select n from Note n where n.belongsTo.pid = ?1 order by n.nid")
    List<NoteView> findByPid(Long pid);

    interface NoteView {
        Long getNid();
        Long getSequence();
        Boolean getIsMain();
        String getTitle();
        ProjectRepository.PidView getBelongsTo();
        List<UserNameView> getAdmins();
    }
    interface NidView {
        Long getNid();
    }

}
