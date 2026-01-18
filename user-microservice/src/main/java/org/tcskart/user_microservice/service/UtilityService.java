package org.tcskart.user_microservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tcskart.user_microservice.entity.User;
import org.tcskart.user_microservice.exception.InvalidUserException;
import org.tcskart.user_microservice.repository.UserRepository;

@Service
public class UtilityService {
     
	@Autowired
	UserRepository repo;
	
	public User findById(Long id) {
	    Optional<User> user=repo.findById(id);
	    if(user.isEmpty()) throw new InvalidUserException("Invalid token, user not found");
	    return user.get();
	}
	
}
