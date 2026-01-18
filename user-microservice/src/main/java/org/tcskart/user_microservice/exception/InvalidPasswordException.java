package org.tcskart.user_microservice.exception;

public class InvalidPasswordException extends RuntimeException{
	
	public InvalidPasswordException(String message) {
		super(message);
	}

}
