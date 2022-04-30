package com.urarik.notes_server.message;

import com.urarik.notes_server.project.ProjectRepository.TitleView;
import com.urarik.notes_server.view.UserNameView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends CrudRepository<Invitation, Long> {
    @Query(value = "select i from Invitation i where i.receiver.username = ?1")
    List<InvitationView> findInvitationByReceiver(String userName);

    @Query(value = "select i from Invitation i where i.iid = ?1")
    Optional<InvitationView> findInvitationByIid(Long iid);

    interface InvitationView {
        Long getIid();
        UserNameView getSender();
        UserNameView getReceiver();
        TitleView getProject();
    }
}

