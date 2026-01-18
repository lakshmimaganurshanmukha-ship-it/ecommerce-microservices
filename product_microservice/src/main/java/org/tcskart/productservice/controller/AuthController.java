package org.tcskart.productservice.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

//    private static final String SECRET_KEY = "mySecretKey123456789012345678901234567890"; // must match filter
	private static final String SECRET_KEY =	"F9m7XZ2o1GJg8kvlHtL5s2n9QwbEfnZ72XUOpdKsR3t5I8uGfa1y7QYpaFmzmwr3";
	private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days


    @PostMapping("/token")
    public Map<String, String> generateToken(@RequestBody Map<String, Object> payload) {
        String email = payload.get("email").toString();
        List<String> roles = (List<String>) payload.get("roles");

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", roles)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return Map.of("token", token);
    }
}

