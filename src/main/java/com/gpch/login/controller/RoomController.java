package com.gpch.login.controller;

import java.util.HashMap;
import java.util.List;
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
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private UserService userService;
    
    @RequestMapping(value = "/joined", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> get(HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	List<Map<String, Object>> rooms = userService.getRoomsJoined(user.getUsername());
	
        result.put("code", 1);
		result.put("message", HttpStatus.OK.name());
		result.put("data", rooms);
        
        return result;
    }


}
