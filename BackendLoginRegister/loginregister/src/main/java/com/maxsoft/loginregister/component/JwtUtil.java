package com.maxsoft.loginregister.component;
import io.jsonwebtoken.*;
import java.security.Key;
import java.util.Date;


import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final String SK="miSuperClaveSecreta12345678901234567890";
    private final long Tiempo_expiracion= 1000 * 60 *60;//1 hora

    private Key obtenerllavefirmada(){
        return Keys.hmacShaKeyFor(SK.getBytes());

    }

    public Claims extraerTodasClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(obtenerllavefirmada())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public boolean EsNToken(String token){
        Claims reclamar= extraerTodasClaims(token);
        String tipo= reclamar.get("tipo", String.class);
        return "NT".equals(tipo);

    }

    public String extraerNombreusuario(String token){
        return extraerTodasClaims(token).getSubject();
    }

    public String JwtAcceso(String nombreusuario){
        return Jwts.builder()
                .setSubject(nombreusuario)
                .claim("tipo","Acceso")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+Tiempo_expiracion))
                .signWith(obtenerllavefirmada(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String JwtNT(String nombreusuario){
        return Jwts.builder()
                .setSubject(nombreusuario)
                .claim("tipo","NT")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+(1000*60*60*24*7)))
                .signWith(obtenerllavefirmada(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean ValidarToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(obtenerllavefirmada()).build().parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }


}
