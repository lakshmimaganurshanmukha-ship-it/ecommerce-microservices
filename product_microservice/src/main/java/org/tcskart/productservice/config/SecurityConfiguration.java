package org.tcskart.productservice.config;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

	@Autowired
	JwtFilter jwtFilter;

	private final static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public static Key getKey() {
		return key;
	}

	@SuppressWarnings("removal")
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
		.csrf(csrf -> 
		csrf.disable())
				.authorizeHttpRequests(request -> request.requestMatchers("/users/login", "/users/register").permitAll()
						.requestMatchers(HttpMethod.GET, "/**").permitAll() // ✅ allow all GET
						.requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/**").permitAll().anyRequest().authenticated())
				.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
					response.getWriter().write("Unauthorized access!");
				});
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of("http://localhost:3000"));
	    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
	    config.setAllowCredentials(true);
	    config.setAllowedHeaders(List.of("*"));

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}
}

