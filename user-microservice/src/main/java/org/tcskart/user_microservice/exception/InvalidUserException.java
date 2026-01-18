package org.tcskart.user_microservice.exception;

public class InvalidUserException extends RuntimeException{
	
	public InvalidUserException(String message) {
		super(message);
	}

}
