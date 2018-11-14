package com.gpch.login.service;

import com.gpch.login.constant.RoomRoleConstant;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.RoomRepository;
import com.gpch.login.repository.RoomRoleRepository;
import com.gpch.login.repository.RoomUserReposity;
import com.gpch.login.repository.UserRepository;

import net.minidev.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("RoomService")
public class RoomService{
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RoomRepository roomRepository;
    
	@Autowired
    private RoomUserReposity roomUserRepository;
	
	@Autowired
    private RoomRoleRepository roomRoleRepository;
    
    @Autowired
    public RoomService(RoomRepository roomRepository,
    		RoomUserReposity roomUserReposity) {
        this.roomRepository = roomRepository;
        this.roomUserRepository = roomUserReposity;
    }
    
    public Boolean checkInRoom(int roomId, User user) {
    	List<RoomUser> roomUsers = roomUserRepository.findByUser(user);
    	//List<RoomUser> roomUsers1 = roomUserRepository.findByUserIdAndRoomId(user.getId(), roomId);
    	System.out.println("checkInRoom:: size1="+roomUsers.size());
    	
    	for(RoomUser roomUser: roomUsers) {
    		if(roomUser.getDeleted()!= 1 && roomUser.getRoom().getId()==roomId) {
    			return true;
    		}
    	}
    	return false;
	}
    
    public List<Map<String, Object>> getMemberRoom(int room_id) {
    	Room room = roomRepository.findById(room_id);
    	
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		if(room==null) {
			return null;
		} else {
			
			Set<RoomUser> roomsUser = room.getMemberRooms();
			for(RoomUser roomUser: roomsUser) {
				if(roomUser.getDeleted() != 1) {
					
					Map<String, Object> m = new HashMap<String, Object>();
					List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
					m.put("userId", roomUser.getUser().getId());
					m.put("username", roomUser.getUser().getUsername());
					m.put("firstName", roomUser.getUser().getFirstName());
					m.put("lastName", roomUser.getUser().getLastName());
					
					Set<RoomRole> rls = roomUser.getRoles();
				
						for(RoomRole rl: rls) {
							if(rl != null) {
								Map<String, Object> rlm = new HashMap<String, Object>();
								rlm.put("code", rl.getCode());
								rlm.put("name", rl.getName());
								roles.add(rlm);
							}
						}
					
					m.put("roles", roles);
					r.add(m);
				}
			}
		}
		
		return r;
	}
    
    public Room createRoom(String name, String description, int maxUser, User user) {
    	Room room = new Room();
    	room.setName(name);
    	room.setActive(1);
    	room.setMaxUser(maxUser);
    	room.setNumber(1);
    	room.setDescription(description);
    	room.setUser(user);
    	room.setCreatedDTG(new Timestamp(new Date().getTime()));
    	room.setUpdatedDTG(new Timestamp(new Date().getTime()));
    	room.setUpdatedBy(user.getId());
    	
    	room = roomRepository.save(room);
    	
    	RoomUser roomUser = new RoomUser();
    	roomUser.setDeleted(0);
    	roomUser.setUser(user);
    	roomUser.setUserCreated(user);
    	roomUser.setCreatedDTG(new Timestamp(new Date().getTime()));
    	roomUser.setRoom(room);
    	roomUser.setUpdatedBy(user.getId());
    	roomUser.setUpdatedDTG(new Timestamp(new Date().getTime()));
    	
    	List<RoomRole> roomroles = new ArrayList<RoomRole>();
    	
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.ADD_MEMBER));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.READ));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.WRITE));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.EXPORT));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.DELETE_MEMBER));
    	
    	roomUser.setRoles(new HashSet<RoomRole>(roomroles));
    	
    	Set<RoomUser> members = new HashSet<RoomUser>();
    	members.add(roomUser);
    	room.setMemberRooms(members);
    	
    	roomUser = roomUserRepository.save(roomUser);
    	room = roomRepository.save(room);
		
		return room;
	}
    
    public Map<String, Object> getRoom(int roomId) {
    		Map<String, Object> room = new HashMap<String, Object>();
    		Map<String, Object> own = new HashMap<String, Object>();
			List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
			
			Room r = roomRepository.findById(roomId);
			if(r!= null && r.getActive()!=0) {
				room.put("id", r.getId());
				room.put("code", r.getCode());
				room.put("name", r.getName());
				room.put("maxUser", r.getMaxUser());
				room.put("description", r.getDescription());
				room.put("number", r.getNumber());
				room.put("active", r.getActive());
				
				own.put("username", r.getUser().getUsername());
				own.put("firstName", r.getUser().getFirstName());
				own.put("lastName", r.getUser().getLastName());
				
				List<Map<String, Object>> members = getMemberRoom(roomId);
				
				room.put("own", own);
				room.put("members", members);
				
			}
		
		return room;
	}
    
    public RoomUser addMemberRoom(int roomId, int userId, List<String> roles, int byUserId) {
		Room room = roomRepository.findById(roomId);
		
		Set<RoomRole> rolesBy = getRoleRoomByUser(roomId, byUserId);
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.ADD_MEMBER)) {
				RoomUser roomUser = new RoomUser();
				User user = userRepository.findById(userId);
				User userBy = userRepository.findById(byUserId);
				

				if(checkInRoom(roomId, user)) {
					return null;
				}
				
				if(user==null) {
					return null;
				}
				
		    	roomUser.setDeleted(0);
		    	roomUser.setUser(user);
		    	roomUser.setUserCreated(userBy);
		    	roomUser.setCreatedDTG(new Timestamp(new Date().getTime()));
		    	roomUser.setRoom(room);
		    	roomUser.setUpdatedBy(user.getId());
		    	roomUser.setUpdatedDTG(new Timestamp(new Date().getTime()));
		    	
		    	List<RoomRole> roomroles = new ArrayList<RoomRole>();
		    	
		    	for(String role: roles) {
		    		RoomRole r = roomRoleRepository.findByName(role);
		    		if(r != null) {
		    			roomroles.add(r);
		    		}
		    	}
		    	
		    	roomUser.setRoles(new HashSet<RoomRole>(roomroles));
		    	
		    	room.getMemberRooms().add(roomUser);
		    	room = roomRepository.save(room);
		    	
		    	updateNumberMemberRoom(roomId);
		    	
		    	return roomUser;
			}
		}
		
		return null;
	}
    
    public RoomUser removeMemberRoom(int roomId, int userId, int byUserId) {
		
		Set<RoomRole> rolesBy = getRoleRoomByUser(roomId, byUserId);
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.DELETE_MEMBER)) {
				
				User user = userRepository.findById(userId);
				User userBy = userRepository.findById(byUserId);
				

				if(!checkInRoom(roomId, user)||user==null||userBy==null) {
					return null;
				}
				
				Set<RoomUser> roomsJoined = user.getMemberRooms();
				
				for(RoomUser roomJoined: roomsJoined) {
					if(roomJoined.getDeleted()!= 1 && roomJoined.getRoom().getId()==roomId) {
						roomJoined.setDeleted(1);
						roomJoined.setUpdatedBy(byUserId);
						roomJoined.setUpdatedDTG(new Timestamp(new Date().getTime()));
						roomUserRepository.save(roomJoined);
						
						updateNumberMemberRoom(roomId);
						
						return roomJoined;
					}
				}
			}
		}
		
		return null;
	}
    
public Room removeRoom(int roomId, int byUserId) {
		
		Room room = roomRepository.findById(roomId);
		User byUser = userRepository.findById(byUserId);
		if(room==null||room.getUser().getId()!=byUser.getId()) {
			return null;
		}
		
		room.setUpdatedBy(byUserId);
		room.setUpdatedDTG(new Timestamp(new Date().getTime()));
		
		room.setActive(0);
		
		roomRepository.save(room);
		
		return room;
	}
    
    private void updateNumberMemberRoom(int roomId) {
    	Room room = roomRepository.findById(roomId);
    	room.setNumber(room.getMemberRooms().size()); 
    }
    
    private Set<RoomRole> getRoleRoomByUser(int roomId, int userId) {
		Room room = roomRepository.findById(roomId);
		
		Set<RoomUser> members = room.getMemberRooms();
		for(RoomUser member: members) {
			if(member.getUser().getId()==userId) {
				return member.getRoles();
			}
		}
		return null;
	}

}