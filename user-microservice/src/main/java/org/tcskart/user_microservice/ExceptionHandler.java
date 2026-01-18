package org.tcskart.user_microservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.tcskart.user_microservice.exception.InvalidPasswordException;
import org.tcskart.user_microservice.exception.InvalidUserException;
import org.tcskart.user_microservice.exception.UserExistException;
import org.tcskart.user_microservice.exception.UserLoggedOut;
import org.tcskart.user_microservice.exception.WeakPasswordException;

@RestControllerAdvice
public class ExceptionHandler {
	
	@org.springframework.web.bind.annotation.ExceptionHandler(UserExistException.class)
	public ResponseEntity<?> userExistException(UserExistException userExistException) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(userExistException.getMessage()); 
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler(WeakPasswordException.class)
	public ResponseEntity<?> weakPasswordException(WeakPasswordException weakPasswordException) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(weakPasswordException.getMessage());
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<?> inavlidUserException(InvalidUserException invalidUserException) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(invalidUserException.getMessage());
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<?> invalidPasswordException(InvalidPasswordException invalidPasswordException) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalidPasswordException.getMessage());
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler(UserLoggedOut.class)
    public ResponseEntity<Object> userLoggedOut(UserLoggedOut exception){
		return new ResponseEntity<>("User logged out++",HttpStatus.UNAUTHORIZED);
	}
	@org.springframework.web.bind.annotation.ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException exception) {
	    return new ResponseEntity<>(exception.getReason(), HttpStatus.UNAUTHORIZED);
	}

}
