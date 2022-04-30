package com.urarik.notes_server.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("not found loginId : "  + username)
        );

        // https://programmer93.tistory.com/68
        UserDetails user = new org.springframework.security.core.userdetails.User(
                username,
                appUser.getPassword(),
                AuthorityUtils.createAuthorityList(appUser.getRole())
                );
        return user;
    }
}
