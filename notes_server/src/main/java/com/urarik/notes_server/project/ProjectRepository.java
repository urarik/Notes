package com.urarik.notes_server.project;

import com.urarik.notes_server.view.UserNameView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    @Query(value = "select distinct p from Project p left outer join p.member m left outer join p.admin a " +
            "where p.owner.username = :username or m.username = :username or a.username = :username")
    List<ProjectView> findByUsername(String username);

    interface ProjectView {
        Long getPid();
        String getTitle();
        UserNameView getOwner();
        Set<UserNameView> getAdmin();
    }

    @Query(value = "select p from Project p where p.pid = ?1")
    Optional<Project> findByPid(Long pid);

    interface PidView {
        Long getPid();
    }

    @Query(value = "select distinct p.pid from Project p left outer join p.member m left outer join p.admin a " +
            "where p.owner.username = :username or m.username = :username or a.username = :username")
    Set<Object> findPidByUsername(@Param("username") String username);

    interface TitleView {
        Long getPid();
        String getTitle();
    }
}
