package com.spring.webflux.security.app.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JWTService {

    final private SecretKey key;
    final private JwtParser parser;

    public  JWTService(){
        this.key = Keys.hmacShaKeyFor("12oi3nu192e932r9u3hd9iwjeq9inefu9nf8eun23e9uqwd".getBytes());
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    // Regresa un jwt token basado en el nombre de usuario
    public String generate(String username){
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(key);

        return builder.compact();
    }

    //La siguiente función es para obtener el nombre de usuario del token
   public String getUserName(String token){
        Claims claims = parser.parseClaimsJws(token).getBody();// Regresa una lista de los claims que tiene el token
       return claims.getSubject();
   }

   public boolean validate(UserDetails user, String token){
        Claims claims = parser.parseClaimsJws(token).getBody();

        boolean unexpired = claims.getExpiration().after(Date.from(Instant.now()));

        return unexpired && user.getUsername() == claims.getSubject();
   }
}
