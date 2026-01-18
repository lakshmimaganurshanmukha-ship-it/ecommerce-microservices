package org.tcskart.order.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtilValidateToken tokenValidater;
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			String authHeader = request.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			
			if(tokenValidater.getLoggedOutToken(token)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has logged out, login again!");
			}
			
			Long userId = tokenValidater.getClaimId(token); // getting our id,role by parsing token
			String role = tokenValidater.getClaimRole(token);
            
			// this is assigning roles
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null,
					List.of(new SimpleGrantedAuthority("ROLE_" + role)));
			// adds extra information here we added: request details
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// storing our authentication token in our spring security context
			// @PreAuthorize detects this specific role from token which we stored in spring
			// security context
			SecurityContextHolder.getContext().setAuthentication(authToken);

		}

		filterChain.doFilter(request, response);

	}

}
