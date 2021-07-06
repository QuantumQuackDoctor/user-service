package com.ss.user.util.jwt;

import com.database.ormlibrary.user.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String createJwt(UserEntity userEntity) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userEntity.getId());
        claims.put("role", userEntity.getUserRole().getRole());

        return Jwts.builder().setSubject(userEntity.getEmail())
                .addClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

}
