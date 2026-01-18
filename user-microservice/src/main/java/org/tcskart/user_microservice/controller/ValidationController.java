package org.tcskart.user_microservice.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tcskart.user_microservice.configuration.JwtUtilGenerateToken;
import org.tcskart.user_microservice.dto.*;
import org.tcskart.user_microservice.entity.User;
import org.tcskart.user_microservice.service.UserService;

@RestController
@RequestMapping("/users")
public class ValidationController {

	private UserService userService;
	private JwtUtilGenerateToken tokenGenerator;

	public ValidationController(UserService userService,JwtUtilGenerateToken tokenGenerator) {
		this.userService = userService;
		this.tokenGenerator=tokenGenerator;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterUser registerUser) {
		User user = userService.registerUser(registerUser);
		if (user != null) {
			return ResponseEntity.status(HttpStatus.OK).body("User Registered Sucessfully...!!!");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Operation Failed");

	}
	@PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user){
   	    return userService.authUser(user);
    }



}
