package com.gpch.login.repository;

import com.gpch.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("UserRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	User findById(int id);
	User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}