package org.tcskart.user_microservice.configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcskart.user_microservice.entity.User;
import org.tcskart.user_microservice.exception.UserLoggedOut;
import org.tcskart.user_microservice.service.UserService;
import org.tcskart.user_microservice.service.UtilityService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtilValidateToken {

	@Value("${jwt.secret.key}")
	private String secretKey;
	private ArrayList<String> loggedOutToken=new ArrayList<>();
	@Autowired
	UtilityService service;
	
	public Boolean getLoggedOutToken(String token) {
		if(token==null)
		{
			return false;
		}
		return loggedOutToken.contains(token);
	}
    public Boolean setLoggedOutToken(String token) {
    	loggedOutToken.add(token);
    	return loggedOutToken.contains(token);
    }
	private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
	
	public Claims validateAndExtractClaims(String token) {
		try {
			
			Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);

			Date expirationDate = claimsJws.getBody().getExpiration();
			
			if (expirationDate != null && expirationDate.before(new Date())) {
				throw new RuntimeException("JWT token has expired");
			}
			return claimsJws.getBody();
		} catch (JwtException | IllegalArgumentException e) {
			throw new RuntimeException("JWT token is invalid or expired: " + e.getMessage(), e);
		}
	}

	public Long getClaimId(String token) {
		Long id=validateAndExtractClaims(token).get("id", Long.class);
	    service.findById(id);
		return id;
	}

	public String getClaimRole(String token) {
	      String role=validateAndExtractClaims(token).get("role", String.class);
	      return role;
	}
}
