//package org.tcskart.productservice.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.security.Key;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final String SECRET = "mySecretKey123456789012345678901234567890"; // should match token issuer
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response); // skip if no token
//            return;
//        }
//
//        String token = authHeader.replace("Bearer ", "");
//        try {
//            Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
//
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String username = claims.getSubject();
//            Object rolesClaim = claims.get("role"); // make sure this matches your token format
//
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            if (rolesClaim instanceof List<?>) {
//                for (Object role : (List<?>) rolesClaim) {
//                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
//                }
//            } else if (rolesClaim instanceof String) {
//                authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesClaim.toString()));
//            }
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(username, null, authorities);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } catch (JwtException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            new ObjectMapper().writeValue(response.getWriter(), "Invalid or expired JWT");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
//
//=======
//package org.tcskart.productservice.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.security.Key;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final String SECRET = "mySecretKey123456789012345678901234567890"; // should match token issuer
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response); // skip if no token
//            return;
//        }
//
//        String token = authHeader.replace("Bearer ", "");
//        try {
//            Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
//
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String username = claims.getSubject();
//            Object rolesClaim = claims.get("roles"); // make sure this matches your token format
//
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            if (rolesClaim instanceof List<?>) {
//                for (Object role : (List<?>) rolesClaim) {
//                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
//                }
//            } else if (rolesClaim instanceof String) {
//                authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesClaim.toString()));
//            }
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(username, null, authorities);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } catch (JwtException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            new ObjectMapper().writeValue(response.getWriter(), "Invalid or expired JWT");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
//
