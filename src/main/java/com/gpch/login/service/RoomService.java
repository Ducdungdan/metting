package com.gpch.login.service;

import com.gpch.login.constant.RoomRoleConstant;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomCode;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomSpeaker;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.RoomCodeRepository;
import com.gpch.login.repository.RoomRepository;
import com.gpch.login.repository.RoomRoleRepository;
import com.gpch.login.repository.RoomUserReposity;
import com.gpch.login.repository.UserRepository;

import net.minidev.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.hamcrest.CoreMatchers.nullValue;
import info.debatty.java.stringsimilarity.Damerau;

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
    private RoomCodeRepository roomCodeRepository;
    
	
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
    		if(roomUser.getRoom().getDeleted() !=1 && roomUser.getDeleted()!= 1 && roomUser.getRoom().getId()==roomId) {
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
			if(roomsUser==null) {
				return r;
			}
			
			for(RoomUser roomUser: roomsUser) {
				if(roomUser.getDeleted() != 1) {
					if(roomUser.getRoom().getDeleted()==1) {
						continue;
					}
					
					Map<String, Object> m = new HashMap<String, Object>();
					List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
					m.put("userId", roomUser.getUser().getId());
					m.put("username", roomUser.getUser().getUsername());
					m.put("firstName", roomUser.getUser().getFirstName());
					m.put("lastName", roomUser.getUser().getLastName());
					
					Set<RoomRole> rls = roomUser.getRoles();
					if(rls==null) {
						continue;
					}
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
    
    public List<Map<String, Object>> getRoomSpeaker(int room_id) {
    	Room room = roomRepository.findById(room_id);
    	
		List<Map<String, Object>> rps = new ArrayList<Map<String, Object>>();
		if(room==null||room.getDeleted()==1) {
			return null;
		} else {
			
			Set<RoomSpeaker> roomSpeakers = room.getRoomSpeakers();
			if(roomSpeakers==null) {
				return rps;
			}
			
			for(RoomSpeaker roomSpeaker: roomSpeakers) {
				
				Map<String, Object> rp = new HashMap<String, Object>();
				rp.put("id", roomSpeaker.getId());
				rp.put("firstName", roomSpeaker.getFirstName());
				rp.put("lastName", roomSpeaker.getLastName());
				rp.put("createdBy", roomSpeaker.getCreatedBy());
				rp.put("createdDTG", roomSpeaker.getCreatedDTG());
				
				rps.add(rp);
				
			}
		}
		
		return rps;
	}
    
    public List<Map<String, Object>> addRoomSpeaker(int roomId, int userId, List<Map<String, Object>> speakers) {
    	Room room = roomRepository.findById(roomId);
    	
		if(room==null||room.getDeleted()==1) {
			return null;
		} else {
			
			Set<RoomSpeaker> sps = new HashSet<RoomSpeaker>();
	    	
	    	for(Map<String, Object> speaker: speakers) {
	    		RoomSpeaker sp = new RoomSpeaker();
	    		String firstName = (String) speaker.get("firstName");
	    		String lastName = (String) speaker.get("lastName");
	    		if(firstName==null||lastName==null) {
	    			continue;
	    		}
	    		
	    		sp.setRoomId(room.getId());
	    		sp.setLastName(lastName);
	    		sp.setFirstName(firstName);
	    		sp.setCreatedBy(userId);
	    		sp.setCreatedDTG(new Timestamp(new Date().getTime()));
	    		sp.setUpdatedBy(userId);
	    		sp.setUpdatedDTG(new Timestamp(new Date().getTime()));
	    		sps.add(sp);
	    	}
	    	
	    	if(room.getRoomSpeakers()==null) {
	    		room.setRoomSpeakers(sps);
	    	} else {
	    		room.getRoomSpeakers().addAll(sps);
	    	}
	    	
	    	
	    	room = roomRepository.save(room);
		}
		
		return getRoomSpeaker(roomId);
	}
    
    public String createCodeRoom(int userId, int roomId, List<String> roles) {
    	final int EXPIRE_TIME = 86400000;
    	Room room = roomRepository.findById(roomId);
    	User user = userRepository.findById(userId);
    	
    	if(room==null||user==null) return null;
    	
    	Set<RoomRole> rolesBy = getRoleRoomByUser(roomId, userId);
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.ADD_MEMBER)) {
				RoomCode roomCode = new RoomCode();
		    	String code = (new GenerateCode()).nextString();
		    	roomCode.setCode(code);
		    	roomCode.setCreatedDTG(new Timestamp(new Date().getTime()));
		    	roomCode.setUpdatedDTG(new Timestamp(new Date().getTime()));
		    	roomCode.setRoom(room);
		    	roomCode.setUser(user);
		    	roomCode.setThruDate(new Timestamp(new Date(System.currentTimeMillis() + EXPIRE_TIME).getTime()));
		    	
		    	List<RoomRole> codeRoles = new ArrayList<RoomRole>();
		    	
		    	for(String role: roles) {
		    		RoomRole r = roomRoleRepository.findByName(role);
		    		if(r != null) {
		    			codeRoles.add(r);
		    		}
		    	}
		    	
		    	roomCode.setRoles(new HashSet<RoomRole>(codeRoles));
		    	
		    	roomCode = roomCodeRepository.save(roomCode);
		    	
		    	return  code;
			}
		}
    	return null;
    	
    }
    
    public RoomUser joinRoomByCode(int userId, String code) {
		User user = userRepository.findById(userId);
		RoomCode roomCode = roomCodeRepository.findByCode(code);
		
		if(user==null||roomCode==null) return null;
		
		RoomUser roomUser = new RoomUser();
		
		System.out.println("::roomCodeRole size: " + roomCode.getRoles().size());
		
		User userBy = roomCode.getUser();
		Room room = roomCode.getRoom();

		if(checkInRoom(room.getId(), user)) {
			return null;
		}
		
		if(room.getMaxUser() < room.getNumber() + 1) {
			return null;
		}
		
		if(roomCode.getThruDate().getTime() < new Date().getTime()) {
			return null;
		}
		
    	roomUser.setDeleted(0);
    	roomUser.setUser(user);
    	roomUser.setUserCreated(userBy);
    	roomUser.setCreatedDTG(new Timestamp(new Date().getTime()));
    	roomUser.setRoom(room);
    	roomUser.setUpdatedBy(user.getId());
    	roomUser.setUpdatedDTG(new Timestamp(new Date().getTime()));
    	
    	roomUser.setRoles(roomCode.getRoles());
    	
    	room.getMemberRooms().add(roomUser);
    	room = roomRepository.save(room);
    	
    	updateNumberMemberRoom(room.getId());
    	
    	return roomUser;
	}
    
    public Room createRoom(String name, String description, int maxUser, User user, List<Map<String, Object>> speakers) {
    	Room room = new Room();
    	room.setCode("00000");
    	room.setName(name);
    	room.setActive(1);
    	room.setDeleted(0);
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
    	
    	Set<RoomSpeaker> sps = new HashSet<RoomSpeaker>();
    	
    	for(Map<String, Object> speaker: speakers) {
    		RoomSpeaker sp = new RoomSpeaker();
    		String firstName = (String) speaker.get("firstName");
    		String lastName = (String) speaker.get("lastName");
    		if(firstName==null||lastName==null) {
    			continue;
    		}
    		sp.setRoomId(room.getId());
    		sp.setLastName(lastName);
    		sp.setFirstName(firstName);
    		sp.setCreatedBy(user.getId());
    		sp.setCreatedDTG(new Timestamp(new Date().getTime()));
    		sp.setUpdatedBy(user.getId());
    		sp.setUpdatedDTG(new Timestamp(new Date().getTime()));
    		sps.add(sp);
    	}
    	
    	room.setRoomSpeakers(sps);
    	
    	//roomUser = roomUserRepository.save(roomUser);
    	room = roomRepository.save(room);
		
		return room;
	}
    
    public Map<String, Object> getRoom(int roomId) {
    		Map<String, Object> room = new HashMap<String, Object>();
    		Map<String, Object> own = new HashMap<String, Object>();
			List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
			
			Room r = roomRepository.findById(roomId);
			if(r!= null && r.getActive()!=0&&r.getDeleted()!=1) {
				room.put("id", r.getId());
				room.put("code", r.getCode());
				room.put("name", r.getName());
				room.put("maxUser", r.getMaxUser());
				room.put("description", r.getDescription());
				room.put("number", r.getNumber());
				room.put("active", r.getActive());
				room.put("updatedBy", r.getUpdatedBy());
				room.put("updatedDTG", r.getUpdatedDTG());
				room.put("createdDTG", r.getCreatedDTG());
				
				own.put("userId", r.getUser().getId());
				own.put("username", r.getUser().getUsername());
				own.put("firstName", r.getUser().getFirstName());
				own.put("lastName", r.getUser().getLastName());
				
				
				
				List<Map<String, Object>> members = getMemberRoom(roomId);
				
				room.put("own", own);
				room.put("speakers", getRoomSpeaker(roomId));
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
				
				if(room.getMaxUser() < room.getNumber() + 1) {
					return null;
				}

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
		if(room.getUser().getId()==byUserId) {
			room.setUpdatedBy(byUserId);
			room.setUpdatedDTG(new Timestamp(new Date().getTime()));
			
			room.setDeleted(1);
			
			roomRepository.save(room);			
		}
		
		return room;
	}
    
    public Room finishRoom(int roomId, int byUserId) {
		
		Room room = roomRepository.findById(roomId);
		User byUser = userRepository.findById(byUserId);
		if(room==null||room.getUser().getId()!=byUser.getId()) {
			return null;
		}
		
		if(room.getUser().getId()==byUserId) {
			room.setUpdatedBy(byUserId);
			room.setUpdatedDTG(new Timestamp(new Date().getTime()));
			
			room.setActive(0);
			
			roomRepository.save(room);
		}
		
		return room;
	}
    
    public List<Map<String, Object>> getReporters(int roomId, int userId) {
    	
    	
		Room room = roomRepository.findById(roomId);
		User user = userRepository.findById(userId);
		List<Map<String, Object>> reporters = new ArrayList<Map<String,Object>>();
		if(room==null||user==null) {
			return null;
		}
		
		List<User> allUser = userRepository.findAll();
		Set<RoomUser> roomUsers = room.getMemberRooms();
		if(roomUsers == null) {
			return null;
		}
		
		List<Integer> listUserInRoom = new ArrayList<Integer>();
		for(RoomUser ru: roomUsers) {
			listUserInRoom.add(ru.getUser().getId());
		}
		
		for(User u: allUser) {
			if(u.getActive()==1&&listUserInRoom.indexOf(u.getId()) == -1&&u.getId()!=userId) {
				Map<String, Object> us = new HashMap<String, Object>();
				
				us.put("userId", u.getId());
				us.put("firstName", u.getFirstName());
				us.put("lastName", u.getLastName());
				us.put("username", u.getUsername());
				us.put("createdDTG", u.getCreatedDTG());
				
				reporters.add(us);
			}
		}
		
		return reporters;
	}
    
public List<Map<String, Object>> getReporters(int userId) {
    	
		User user = userRepository.findById(userId);
		List<Map<String, Object>> reporters = new ArrayList<Map<String,Object>>();
		if(user==null) {
			return null;
		}
		
		List<User> allUser = userRepository.findAll();
		
		for(User u: allUser) {
			if(u.getActive()==1&&u.getId()!=userId) {
				Map<String, Object> us = new HashMap<String, Object>();
				
				us.put("userId", u.getId());
				us.put("firstName", u.getFirstName());
				us.put("lastName", u.getLastName());
				us.put("username", u.getUsername());
				us.put("createdDTG", u.getCreatedDTG());
				
				reporters.add(us);
			}
		}
		
		return reporters;
	}
    
    private void updateNumberMemberRoom(int roomId) {
    	Room room = roomRepository.findById(roomId);
    	room.setNumber(room.getMemberRooms().size()); 
    }
    
    public Set<RoomRole> getRoleRoomByUser(int roomId, int userId) {
		Room room = roomRepository.findById(roomId);
		
		Set<RoomUser> members = room.getMemberRooms();
		for(RoomUser member: members) {
			if(member.getUser().getId()==userId) {
				return member.getRoles();
			}
		}
		return null;
	}
    
    private final int latency = 100; //2s
    private final int minDistance = 1;
    private final int minSegment = 200; //tg ngat doan 2s
    private final String delimiters = "\\s+|,\\s*|\\.\\s*";
    
    public List<Map<String, Object>> mergeStenographTranscript(List<Map<String, Object>> listStenograph, List<Map<String, Object>> listTranscript) {
    	List<Map<String, Object>> r = new ArrayList<Map<String,Object>>();
    	listStenograph.sort((Map<String, Object> o1, Map<String, Object> o2)->(int) (long) o1.get("start")-(int) (long)o2.get("start"));
    	listTranscript.sort((Map<String, Object> o1, Map<String, Object> o2)->(int) (long)o1.get("start")-(int) (long)o2.get("start"));
    	
    	int i = 0, j = 0;
    	Map<String, Object> temp = null;
    	while(i < listStenograph.size() && j < listTranscript.size()) {
    		Map<String, Object> stenograph = listStenograph.get(i);
    		Map<String, Object> transcript = listTranscript.get(j);
    		String contentStenograph = (String)stenograph.get("content");
    		String contentTranscript = (String)transcript.get("content");
    		
    		
    		long start1 = (long) stenograph.get("start");
    		long end1 = (long) stenograph.get("end");
    		long start2 = (long) transcript.get("start");
    		long end2 = (long) transcript.get("end");
    		
    		long s = 0;
    		long e = 0;
    		String c = "";
    		
    		
    		
    		if(start1 < start2) {
    			s = start1;
    			e = end1;
    			c = contentStenograph;
    			i++;
    		} else {
    			s = start2;
    			e = end2;
    			c = contentTranscript;
    			j++;
    		}
    		

    		if(temp == null) {
    			temp = new HashMap<String, Object>(); 
				temp.put("start", s);
		    	temp.put("end", e);
		    	temp.put("content", c);
		    	continue;
    		}

    		long start = (long) temp.get("start");
    		long end = (long) temp.get("end");
    		String content = (String) temp.get("content");
    		
    		if(end > s - latency) {// s dan xen hoac long nhau
    			String[] listTempContent = content.split(delimiters);
    			String[] listFirstContent = c.split(delimiters);
    			List<Integer> ends = new ArrayList<Integer>();
    			List<Integer> starts = new ArrayList<Integer>();
				if(end > e - latency) { //2 doan long nhau
					int indexE = -1, indexS = -1;
					for(int k = 0; k < listTempContent.length; ++k ) {
						List<Integer> check = new ArrayList<Integer>();
						
						for(int x = 0; x < listFirstContent.length; ++x) {
							if(listFirstContent[x].toLowerCase().equals(listTempContent[k].toLowerCase())) {
								check.add(x);
								break;
							}
						}
						
						if(check.size() > 0) {
							starts = check;
							indexS = k;
							break;
						}
						
						starts = indexOf(c, listTempContent[k]);
						if(starts.size() > 0) {
							indexS = k;
							break;
						}
					}
					
					for(int k = listTempContent.length - 1; k >= 0; --k ) {
						List<Integer> check = new ArrayList<Integer>();
						for(int x = 0; x < listFirstContent.length; ++x) {
							if(listFirstContent[x].toLowerCase().equals(listTempContent[k].toLowerCase())) {
								check.add(x);
								indexS = k;
								break;
							}
						}
						
						if(check.size() > 0) {
							ends = check;
							indexE = k;
							break;
						}
						
						
						ends = indexOf(c, listTempContent[k]);
						if(ends.size() > 0) {
							indexE = k;
							break;
						}
					}
					
					if(starts.size() == 0 || indexE == -1) {
						temp.put("end", e);
				    	temp.put("content", content + " " + c);
					} else {
						double minD = 999;
						int indexSs = -1, indexEe = -1;
						Damerau d = new Damerau();
						String cc1 = "";
						
						for(int k = indexS; k <= indexE; ++k) {
							cc1 += listTempContent[k] + " ";
						}
						
						for(int k = 0; k < starts.size(); ++k) {
							for(int l = ends.size() - 1; l >= 0; --l) {
								String cc2 = "";
								for(Integer h = starts.get(k); h <= ends.get(l); ++h) {
									cc2 += listFirstContent[h] + " ";
								}
								double dv = d.distance(cc2, cc1);
								if(dv < minD&& dv < 20) {
									minD = dv;
									indexSs = starts.get(k);
									indexEe = ends.get(l);
								}
							}
						}
						
						if(indexSs==-1||indexEe==-1) {
					    	temp.put("content", content + " " + c);
						} else {
							String newContent = "";
							
							int indexA = content.indexOf(listTempContent[indexSs]);
							int indexB = content.lastIndexOf(listTempContent[indexEe]);
							
							newContent += content.substring(0, indexA);
							
							newContent += " " + c + " ";
							
							newContent += content.substring(indexB);
							
					    	temp.put("content", newContent);
							
						}
					}
				} else {//2 doan dan xen
					int indexS = -1;
					int indexE = -1;
					
					if(end > e - latency) { //2 doan long nhau
						for(int k = 0; k < listFirstContent.length; ++k ) {
							List<Integer> check = new ArrayList<Integer>();
							
							for(int x = 0; x < listTempContent.length; ++x) {
								if(listFirstContent[k].toLowerCase().equals(listTempContent[x].toLowerCase())) {
									check.add(x);
									break;
								}
							}
							
							if(check.size() > 0) {
								starts = check;
								indexS = k;
								break;
							}
							
							starts = indexOf(content, listFirstContent[k]);
							if(starts.size() > 0) {
								indexS = k;
								break;
							}
						}
					
					
					for(int k = 0; k < listFirstContent.length; ++k ) {
						starts = indexOf(content, listFirstContent[k]);
						if(starts.size() > 0) {
							indexS = k;
							break;
						}
					}
					
					for(int k = listTempContent.length - 1; k >= 0; --k ) {
						List<Integer> check = new ArrayList<Integer>();
						for(int x = 0; x < listFirstContent.length; ++x) {
							if(listFirstContent[x].toLowerCase().equals(listTempContent[k].toLowerCase())) {
								check.add(x);
								indexS = k;
								break;
							}
						}
						
						if(check.size() > 0) {
							ends = check;
							indexE = k;
							break;
						}
						
						
						ends = indexOf(c, listTempContent[k]);
						if(ends.size() > 0) {
							indexE = k;
							break;
						}
					}
					
					if(indexS == -1 || indexE == -1) {
						temp.put("end", e);
				    	temp.put("content", content + " " + c);
					} else {
						double minD = 999;
						int indexSs = -1, indexEe = -1;
						Damerau d = new Damerau();
						
						for(int k = 0; k < starts.size(); ++k) {
							for(int l = ends.size() - 1; l >= 0; --l) {
								if(starts.get(k) < ends.get(l)) {
									String cc1 = "", cc2 = "";
									
									for(Integer h = starts.get(k); h < indexE; ++h) {
										cc1 += listTempContent[h] + " ";
									}
									
									for(Integer h = indexS; h < ends.get(l); ++h) {
										cc2 += listFirstContent[h] + " ";
									}
									
									double dv = d.distance(cc1, cc2);
									if(dv < minD&& dv < 20) {
										minD = dv;
										indexSs = starts.get(k);
										indexEe = ends.get(l);
									}
								}
							}
						}
						
						if(indexSs==-1||indexEe==-1) {
					    	temp.put("content", content + " " + c);
						} else {
							String newContent = "";
							
							int indexA = content.indexOf(listTempContent[indexSs]);
							int indexB = c.lastIndexOf(listFirstContent[indexEe]);
							
							newContent = content + " " + c.substring(indexB);
							
					    	temp.put("content", newContent);
					    	temp.put("end", e);
							
						}
					}
				}
			}
    		}else {//k dan xen
				if(s - latency - end > minSegment) { // nam 2 doan
					r.add(temp);
					temp = new HashMap<String, Object>(); 
					temp.put("start", s);
			    	temp.put("end", e);
			    	temp.put("content", c);	
				} else {//gop 2 phan thanh 1 doan
					temp.put("end", e);
			    	temp.put("content", content + " " + c);
				}
			}
    	}
    	
    	r.add(temp);
    	if(i < listStenograph.size()) {
    		for(int k = i; k < listStenograph.size(); ++k) {
    			r.add(listStenograph.get(k));
    		}
    	}
    	
    	if(j < listTranscript.size()) {
    		for(int k = j; k < listTranscript.size(); ++k) {
    			r.add(listTranscript.get(k));
    		}
    	}
    	
    	return r;
	}
    
    public List<Integer> indexOf(String o1, String o2) {
    	List<Integer> r = new ArrayList<Integer>();
    	Damerau d = new Damerau();
    	
		String[] list = o1.split(delimiters);
		for(int i = 0; i < list.length; ++i) {
			double distance = d.distance(list[i], o2);
			if(distance < minDistance) {
				r.add(i);
			}
		}
		//r.sort((Integer n1, Integer n2)->n1-n2);
		
		return r;
		
    }

}