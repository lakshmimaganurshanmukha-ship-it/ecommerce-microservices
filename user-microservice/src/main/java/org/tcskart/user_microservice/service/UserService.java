package org.tcskart.user_microservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.tcskart.user_microservice.configuration.JwtUtilGenerateToken;
import org.tcskart.user_microservice.configuration.JwtUtilValidateToken;
import org.tcskart.user_microservice.dto.PasswordChange;
import org.tcskart.user_microservice.dto.RegisterUser;
import org.tcskart.user_microservice.dto.UpdateUser;
import org.tcskart.user_microservice.entity.User;
import org.tcskart.user_microservice.exception.InvalidPasswordException;
import org.tcskart.user_microservice.exception.InvalidUserException;
import org.tcskart.user_microservice.exception.UserExistException;
import org.tcskart.user_microservice.exception.WeakPasswordException;
import org.tcskart.user_microservice.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

	UserRepository userRepository;
    JwtUtilValidateToken tokenValidator;
    JwtUtilGenerateToken tokenGenerator;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserService(UserRepository userRepository,JwtUtilGenerateToken tokenGenerator, JwtUtilValidateToken tokenValidator) {
		this.userRepository = userRepository;
		this.tokenValidator=tokenValidator;
		this.tokenGenerator=tokenGenerator;
	}

	public User registerUser(RegisterUser registerUser) {
	
		String hasedPassword=bCryptPasswordEncoder.encode(registerUser.getPassword());
		User user = userRepository.findByUsername(registerUser.getUsername());
		if (user != null) {
			throw new UserExistException("User Exists Please Login...");
		}

		if (!validatePassword(registerUser.getPassword())) {
			throw new WeakPasswordException("Enter a strong Password");
		}

		User newUser = new User();
		newUser.setName(registerUser.getName());
		newUser.setPassword(hasedPassword);
		newUser.setAddress(registerUser.getAddress());
		newUser.setPhone(registerUser.getPhone());
		newUser.setRole("USER");
		newUser.setUsername(registerUser.getUsername());
		return userRepository.save(newUser);

	}
	
	  public User registerAdmin(RegisterUser registerUser) {

          String hasedPassword=bCryptPasswordEncoder.encode(registerUser.getPassword());
          User user = userRepository.findByUsername(registerUser.getUsername());
          if (user != null) {
                  throw new UserExistException("User Exists Please Login...");
          }

          if (!validatePassword(registerUser.getPassword())) {
                  throw new WeakPasswordException("Enter a strong Password");
          }

          User newUser = new User();
          newUser.setName(registerUser.getName());
          newUser.setPassword(hasedPassword);
          newUser.setAddress(registerUser.getAddress());
          newUser.setPhone(registerUser.getPhone());
          newUser.setRole("ADMIN");
          newUser.setUsername(registerUser.getUsername());
          return userRepository.save(newUser);

  }


	public boolean validatePassword(String password) {
		String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*+=!]).{8,}$";
		return password.matches(regex);
	}

	public ResponseEntity<Object> authUser(User validationRequest) {
		User authenticatedUser=userRepository.findByUsername(validationRequest.getUsername());
		if(authenticatedUser!=null) {
			if(bCryptPasswordEncoder.matches(validationRequest.getPassword(), authenticatedUser.getPassword())) {
				String token=tokenGenerator.generateToken(
						authenticatedUser.getId()
						,authenticatedUser.getUsername()
						,authenticatedUser.getRole());
				return new ResponseEntity<Object>("Bearer "+token,HttpStatus.OK);
			}

		}

			return new ResponseEntity<Object>("Wrong Cerdentials",HttpStatus.BAD_REQUEST);
		
	}
	
	public boolean updateUser(UpdateUser updateUser,HttpServletRequest httpServletRequest) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		Long id=Long.MAX_VALUE;
		try {
		 id=tokenValidator.getClaimId(token);
		}
		catch(Exception e) {
			throw new InvalidUserException("User Not Exist");
		}
		User user=userRepository.getById(id);
		user.setAddress(updateUser.getAddress()!=null?updateUser.getAddress():user.getAddress());
		user.setName(updateUser.getName()!=null?updateUser.getName():user.getName());
		user.setPhone(updateUser.getPhone()!=null?updateUser.getPhone():user.getPhone());
		userRepository.save(user);
		return true;
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
		}
	
	public boolean changePassword(PasswordChange passwordChange, HttpServletRequest httpServletRequest) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
	    Long id=Long.MAX_VALUE;
		try {
		 id=tokenValidator.getClaimId(token);
		}
		catch(Exception e) {
			throw new InvalidUserException("User Not Exist");
		}
		
		Optional<User> user=userRepository.findById(id);
	
		if(bCryptPasswordEncoder.matches(passwordChange.getOldPassword(),user.get().getPassword() )) {
			
			String hasedPassword=bCryptPasswordEncoder.encode(passwordChange.getNewPassword());
			user.get().setPassword(hasedPassword);
		}
		else {
			throw new InvalidPasswordException("Password did not Match");	
		}
		
		return true;
	}
	
	
	
	public boolean logOut(HttpServletRequest request) {
		String token=request.getHeader("Authorization").substring(7);
	    Long id=Long.MAX_VALUE;
		try {
		 id=tokenValidator.getClaimId(token);
		}
		catch(Exception e) {
			throw new InvalidUserException("User Not Exist");
		}
		return tokenValidator.setLoggedOutToken(token);
	}
}
