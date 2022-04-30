package com.urarik.notes_server.security;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class JwtAuthenticationService {
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("as@sXvcDSXAqwq!".getBytes());
    private static final String PREFIX = "Bearer";
    private static final String EMPTY = "";
    private static final long EXPIRATION_TIME = (long) 8.64e7; //a day
    private static final String AUTHORIZATION = "Authorization";

    private final UserDetailsServiceImpl userDetailsService;
    private final UserInfo userInfo;

    public JwtAuthenticationService(UserDetailsServiceImpl userDetailsService, UserInfo userInfo) {
        this.userDetailsService = userDetailsService;
        this.userInfo = userInfo;
    }

    public String createToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = resolveToken(request);
        if(token != null && validateToken(token)) {
            String username = getUsername(token);
            userInfo.setUsername(username);
            if(username != null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                // token for next filter in filter chain; Username...Filter
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }
        }
        return null;
    }

    public String getEncodedPassword(String rawPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    private String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION);
        if(bearerToken != null && bearerToken.startsWith(PREFIX))
            return bearerToken.replace(PREFIX, EMPTY).trim();
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch(JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Expired or invalid JWT token");
        }
    }
}
