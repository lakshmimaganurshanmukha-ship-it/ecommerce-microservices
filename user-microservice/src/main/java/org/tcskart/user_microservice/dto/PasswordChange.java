package org.tcskart.user_microservice.dto;

import lombok.Data;

@Data
public class PasswordChange {
	
	private String oldPassword;
	private String newPassword;

}
