package org.tcskart.user_microservice.exception;

public class WeakPasswordException  extends RuntimeException{
	public WeakPasswordException(String message){
		super(message);
		
	}

}
