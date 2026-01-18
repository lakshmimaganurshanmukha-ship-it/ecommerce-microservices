package org.tcskart.user_microservice.dto;

import lombok.Data;

@Data
public class ValidationRequest {
	private String username;
	private String password;

}
