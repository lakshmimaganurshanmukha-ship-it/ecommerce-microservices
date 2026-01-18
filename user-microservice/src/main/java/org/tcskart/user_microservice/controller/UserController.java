package org.tcskart.user_microservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tcskart.user_microservice.configuration.JwtUtilGenerateToken;
import org.tcskart.user_microservice.dto.PasswordChange;
import org.tcskart.user_microservice.dto.UpdateUser;
import org.tcskart.user_microservice.entity.User;
import org.tcskart.user_microservice.service.UserService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@EnableMethodSecurity(prePostEnabled = true)
@RequestMapping("/users")
public class UserController {

	UserService userService;
	
	public UserController(UserService service) { 
		this.userService=service;
	}
	
	@PutMapping("/update")
	public ResponseEntity<?> updateUser(@RequestBody UpdateUser updateUser,HttpServletRequest httpServletRequest) {
		userService.updateUser(updateUser, httpServletRequest);
		return ResponseEntity.status(HttpStatus.OK).body("User Updated");	
	}
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> viewAllUsers() {
		List<User> users=userService.getAllUsers();
		return ResponseEntity.status(HttpStatus.OK).body(users);
	}
	
//	@DeleteMapping("/users/delete/{userName}")
//	@PreAuthorize("hasRole('ADMIN')")
//	public void deleteUser(@PathVariable String userName) {
//		userService.deleteUser(userName);
//		
//	}
	
	@GetMapping("/jwt")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> jwtAuth(){
		return new ResponseEntity<Object>("Jwt based login",HttpStatus.OK);
	}
	
	@PatchMapping("/password")
	public ResponseEntity<?> setPassword(@RequestBody PasswordChange passwordChange,HttpServletRequest httpServletRequest) {
		userService.changePassword(passwordChange, httpServletRequest);
		return ResponseEntity.status(HttpStatus.OK).body("Password Changed Sucessfully...");
	}
    
	@GetMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletRequest request){
	     return userService.logOut(request)?
	    		 ResponseEntity.status(HttpStatus.OK).body("Succesfully logged out")
	    		 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Didn't able to log out");
	}
}
