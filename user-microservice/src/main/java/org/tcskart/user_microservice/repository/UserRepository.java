package org.tcskart.user_microservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tcskart.user_microservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	public User findByUsername(String userName);
	public Optional<User> findByUsernameAndPassword(String username,String password);

}
