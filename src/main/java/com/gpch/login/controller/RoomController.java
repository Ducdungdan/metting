package com.gpch.login.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.gpch.login.model.Room;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.service.JwtService;
import com.gpch.login.service.RoomService;
import com.gpch.login.service.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @Autowired
    private RoomService roomService;
    
    @RequestMapping(value = "/joined", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getRoomJoined(HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	List<Map<String, Object>> rooms = userService.getRoomsJoined(user.getUsername());
	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", rooms);
        
        return result;
    }
    
    @RequestMapping(value = "/members/{roomId}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getRoomMembers(@PathVariable int roomId, HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	List<Map<String, Object>> members = null;
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		members = roomService.getMemberRoom(roomId);
    	}
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", members);
        
        return result;
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> createRoom(HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> room = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	String name = request.getParameter("name");
    	String description = request.getParameter("description");
    	String sMaxUser = request.getParameter("maxUser");
    	if(name.isEmpty()||description.isEmpty()||sMaxUser.isEmpty()) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            return result;
    	}
    	int maxUser = 0;
    	try {
    		 maxUser = Integer.valueOf(sMaxUser);
    	}catch (Exception e) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            return result;
		}
    	    	
    	Room r = roomService.createRoom(name, description, maxUser, user);
    	
    	room = roomService.getRoom(r.getId());
    	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", room);
        
        return result;
    }
    
    @RequestMapping(value = "/{roomId}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getRoom(@PathVariable int roomId, HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> room = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		room = roomService.getRoom(roomId);    		
    	}
    	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", room);
        
        return result;
    }
    
    @RequestMapping(value = "/add-members", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> addMembers(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> room = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("members")||!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	int roomId = Integer.valueOf((String) payload.get("roomId"));
    	List<Object> members = (List<Object>) payload.get("members");
    	
    	for(int i = 0; i < members.size(); ++i) {
    		Map<String, Object> member = (Map<String, Object>) members.get(i);
    		
    		List<String> roles = (List<String>) member.get("roles");
    		int usrId = Integer.valueOf((String) member.get("userId"));
    		roomService.addMemberRoom(roomId, usrId, roles, user.getId());
    	}
    	
    	
    	List<Map<String, Object>> listMember = null;
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		listMember = roomService.getMemberRoom(roomId);
    	}
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", listMember);
        
        return result;
    	
    	
    }
    
    @RequestMapping(value = "/remove-members", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> removeMembers(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("members")||!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	List<Object> members = (List<Object>) payload.get("members");
    	
    	for(int i = 0; i < members.size(); ++i) {
    		
    		roomService.removeMemberRoom(roomId, (int)members.get(i), user.getId());
    	}
    	
    	
    	List<Map<String, Object>> listMember = null;
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		listMember = roomService.getMemberRoom(roomId);
    	}
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", listMember);
        
        return result;
    	
    }
    
    @RequestMapping(value = "/remove-room", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> removeRoom(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	
    	Room room = roomService.removeRoom(roomId, user.getId());
    	
    	if(room ==null) {
    		if(!payload.containsKey("roomId")) {
        		result.put("code", 1);
        		result.put("message", "Not accesss room");
                
                return result;
        	}
    	}
    	Map<String, Object> r = new HashMap<String, Object>();
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		r = roomService.getRoom(roomId);    		
    	}
    	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", room);
        
        return result;
    	
    }


}
