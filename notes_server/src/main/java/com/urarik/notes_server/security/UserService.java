package com.urarik.notes_server.security;

import com.urarik.notes_server.view.UserNameView;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUser(String userName) {
        return userRepository.findByUsername(userName);
    }

    public Set<User> findByUserNameIn(List<String> userName) {
        if(userName == null) userName = new ArrayList<>();
        return userRepository.findByUsernameIn(userName);
    }

    public Set<UserNameView> findByUserNameLike(String prefix) {
        return new HashSet<>(userRepository.findByUserNameLike(PageRequest.of(0, 10), prefix));
    }
}
