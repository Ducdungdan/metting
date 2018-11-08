package com.gpch.login.service;

import com.gpch.login.model.Role;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.RoomRepository;
import com.gpch.login.repository.RoomUserReposity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("RoomService")
public class RoomService{
	
	@Autowired
    private RoomRepository roomRepository;
    
	@Autowired
    private RoomUserReposity roomUserRepository;
    
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
    		if(roomUser.getRoom().getId()==roomId) {
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
					m.put("username", roomUser.getUser().getUsername());
					m.put("firstName", roomUser.getUser().getFirstName());
					m.put("lastName", roomUser.getUser().getLastName());
					
					Set<RoomRole> rls = roomUser.getRoles();
					
					for(RoomRole rl: rls) {
						Map<String, Object> rlm = new HashMap<String, Object>();
						rlm.put("code", rl.getCode());
						rlm.put("name", rl.getName());
						roles.add(rlm);
					}
					
					
					m.put("roles", roles);
					
					r.add(m);
				}
			}
		}
		
		return r;
	}

}