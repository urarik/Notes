package com.urarik.notes_server.security;

import com.urarik.notes_server.security.dto.AccountCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.*;

@RestController
public class AuthenticationController {
    AuthenticationManager authenticationManager;
    JwtAuthenticationService jwtAuthenticationService;
    UserRepository userRepository;
    UserInfo userInfo;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtAuthenticationService jwtAuthenticationService, UserRepository userRepository, UserInfo userInfo) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.userRepository = userRepository;
        this.userInfo = userInfo;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<Object, Object>> signIn(@RequestBody AccountCredentials credentials) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword())
            );
            List<String> roles = new ArrayList<>();

            roles.add(userRepository.findByUsername(credentials.getUsername())
                    .orElseThrow(
                            () -> new UsernameNotFoundException("Username " + credentials.getUsername() + "not found")
                    ).getRole()
                    );
            String token = jwtAuthenticationService.createToken(credentials.getUsername(), roles);

            Map<Object, Object> model = new HashMap<>();
            model.put("username", credentials.getUsername());
            model.put("token", token);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> signUp(@RequestBody AccountCredentials credentials) {
        Optional<User> exist = userRepository.findByUsername(credentials.getUsername());
        exist.ifPresent(user -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An user name is already existed!");
        });

        String userName = credentials.getUsername();
        String rawPassword = credentials.getPassword();
        String password= jwtAuthenticationService.getEncodedPassword(rawPassword);

        userRepository.save(new User(userName, password, "USER"));
        return ResponseEntity.created(URI.create("")).build();
    }

    @GetMapping("/test")
    public String test() {
        return userInfo.getUsername();
    }

}
