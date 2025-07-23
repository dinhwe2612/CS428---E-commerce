package com.order.order_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Long extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.get("userId", String.class));
    }

    public Collection<GrantedAuthority> extractAuthorities(String token) {
        final Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public UserDetails createUserDetails(String token) {
        String username = extractUsername(token);
        Collection<GrantedAuthority> authorities = extractAuthorities(token);
        return new User(username, "", authorities);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return !isTokenExpired(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            throw new RuntimeException("Token expired or invalid: " + e.getMessage());
        }
    }

    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract token expiration: " + e.getMessage());
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token: " + e.getMessage());
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 