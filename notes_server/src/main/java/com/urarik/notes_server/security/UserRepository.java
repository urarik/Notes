package com.urarik.notes_server.security;

import com.urarik.notes_server.view.UserNameView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Set<User> findByUsernameIn(List<String> usernames);

    @Query(value = "select u from User u where u.username like concat(:prefix, '%')  ")
    List<UserNameView> findByUserNameLike(Pageable pageable, String prefix);

}
