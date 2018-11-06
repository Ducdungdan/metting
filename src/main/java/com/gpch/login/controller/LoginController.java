package com.gpch.login.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import com.gpch.login.model.User;
import com.gpch.login.service.JwtService;
import com.gpch.login.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtService jwtService;
//
//    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
//    public ModelAndView login(){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("login");
//        return modelAndView;
//    }
//
//
//    @RequestMapping(value="/registration", method = RequestMethod.GET)
//    public ModelAndView registration(){
//        ModelAndView modelAndView = new ModelAndView();
//        User user = new User();
//        modelAndView.addObject("user", user);
//        modelAndView.setViewName("registration");
//        return modelAndView;
//    }
//
    
    @RequestMapping(value = "/api/registration", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> createNewUser(@Valid User user, BindingResult bindingResult) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
        User userExists = userService.findUserByUsername(user.getUsername());
	
		if (userExists != null) {
			result.put("code", 1);
			result.put("message", "There is already a user registered with the username provided");
		} else {
        	userService.saveUser(user);
        	result.put("code", 0);
        	result.put("message", "User has been registered successfully");
        }
        
        return result;
    }
//
//    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
//    public ModelAndView home(){
//        ModelAndView modelAndView = new ModelAndView();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = userService.findUserByEmail(auth.getName());
//        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
//        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
//        modelAndView.setViewName("admin/home");
//        return modelAndView;
//    }
    
    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public @ResponseBody Map<String, ? extends Object> login(HttpServletRequest request) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> data = new HashMap<String, Object>();
    	String message="";
    	HttpStatus httpStatus = null;
    	int code = 0;
    	System.out.println("Debug login::" + request.getParameter("username") + ", " +  request.getParameter("password"));
      try {
    	  User user = userService.findByUsernameAndPassword(request.getParameter("username"), request.getParameter("password"));
        if (user!=null) {
          String token = jwtService.generateTokenLogin(user.getUsername());
          result.put("token", token);
          httpStatus = HttpStatus.OK;
          message = httpStatus.name();
          code=0;
          
          data.put("username", user.getUsername());
          data.put("firstName", user.getFirstName());
          data.put("lastName", user.getLastName());
          data.put("phone", user.getPhone());
          result.put("data", data);
          System.out.println("test getAuthorities::" + user.getRoles().size());
        } else {
        	message = "Wrong userId and password";
        	httpStatus = HttpStatus.BAD_REQUEST;
        	code=1;
        }
      } catch (Exception ex) {
    	  message = "Server Error";
    	  httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
      
      result.put("message", message);
      result.put("code", code);
      
      return result;
    }


}
