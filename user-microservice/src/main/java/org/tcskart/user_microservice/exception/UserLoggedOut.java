package org.tcskart.user_microservice.exception;

public class UserLoggedOut extends RuntimeException{
	public UserLoggedOut(String message) {
		super(message);
	}
}
