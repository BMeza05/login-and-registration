package com.brian.login_and_registration.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.brian.login_and_registration.models.User;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {
	
	User findByEmail(String email);
}
