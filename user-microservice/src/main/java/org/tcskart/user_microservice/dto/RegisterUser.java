package org.tcskart.user_microservice.dto;

import lombok.Data;

@Data
public class RegisterUser {
	
	private String username;
	private String name;
	private String phone;
	private String address;
	private String password;

}
