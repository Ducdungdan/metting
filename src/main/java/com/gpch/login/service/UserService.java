package com.gpch.login.service;

import com.gpch.login.model.Role;
import com.gpch.login.model.User;
import com.gpch.login.repository.RoleRepository;
import com.gpch.login.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;

@Service("userService")
public class UserService{

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        user.setCreatedDTG(new Timestamp(System.currentTimeMillis()));
        Role userRole = roleRepository.findByRole("CUSTOMER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }
    
    public User findByUsernameAndPassword(String username, String password) {
    	User user = userRepository.findByUsername(username);
    	if(user == null) return null;
    	Boolean check = bCryptPasswordEncoder.matches(password, user.getPassword());
    	if(check) {
    		return user;
    	} else {
    		return null;
    	}
    	
//    	String passwordEncoder = bCryptPasswordEncoder.encode(password);
//    	return userRepository.findByUsernameAndPassword(username, passwordEncoder);
    	
    }
    
    public User loadUserByUsername(String username) {
        
    	return userRepository.findByUsername(username);
    	
    }
    

}