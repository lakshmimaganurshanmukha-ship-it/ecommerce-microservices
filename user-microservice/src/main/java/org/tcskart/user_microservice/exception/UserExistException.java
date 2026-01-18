package org.tcskart.user_microservice.exception;


public class UserExistException extends RuntimeException {
	
	public UserExistException(String message) {
		super(message);
	}

}
