package com.java.ecom.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final String SECRET = "ECOM_SECRET_KEY";
    private static final long EXPIRATION = 1000*60*60*24; //24 hours

    public String generateToken(UUID userId, String role){
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }

    public UUID extractUserId(String token){
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public String extractRole(String token){
        return extractClaims(token).get("role",String.class);
    }


}
