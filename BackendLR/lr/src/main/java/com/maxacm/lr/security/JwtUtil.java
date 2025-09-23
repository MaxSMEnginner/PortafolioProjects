package com.maxacm.lr.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.List;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "miSuperClaveSecreta12345678901234567890"; // mínimo 32 chars
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        return "REFRESH".equals(type);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String generateAccessToken(UserDetails userDetails) {

        // Obtenemos los roles (authorities) del objeto UserDetails
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roleClaims = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("type","ACCESS")
                .claim("roles", roleClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        // Obtenemos los roles (authorities) del objeto UserDetails
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roleClaims = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("type","REFRESH")
                .claim("roles", roleClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 días
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String Newjwt(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("type","ACCESS")
                .claim("roles", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 días
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String NewRT(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("type","REFRESH")
                .claim("roles", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 días
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
