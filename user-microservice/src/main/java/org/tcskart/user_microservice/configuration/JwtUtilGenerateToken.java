package org.tcskart.user_microservice.configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;



@Component
public class JwtUtilGenerateToken {
	 
	 @Value("${jwt.secret.key}")
	 private String secretKey;
	 
	 private Key getSigningKey() {
	        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	    }
	 
	 public String generateToken(Long id,String username,String role) {
		 
		 return Jwts.builder()
				 .claim("id",id)
				 .claim("username",username)
				 .claim("role",role)
				 .setSubject(username)
				 .setIssuedAt(new Date())
				 .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                 .signWith(getSigningKey())
                 .compact();
	 }
}
