package com.gpch.login.service;

import com.gpch.login.constant.RoomRoleConstant;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomCode;
import com.gpch.login.model.RoomContent;
import com.gpch.login.model.RoomContentReport;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomSpeaker;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.RoomCodeRepository;
import com.gpch.login.repository.RoomContentReportRepository;
import com.gpch.login.repository.RoomContentRepository;
import com.gpch.login.repository.RoomRepository;
import com.gpch.login.repository.RoomRoleRepository;
import com.gpch.login.repository.RoomSpeakerRepository;
import com.gpch.login.repository.RoomUserReposity;
import com.gpch.login.repository.UserRepository;

import net.minidev.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("RoomContentService")
public class RoomContentService{
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RoomRepository roomRepository;
	
	@Autowired
    private RoomContentRepository roomContentRepository;
    
	@Autowired
    private RoomContentReportRepository roomContentReportRepository;
	
	@Autowired
    private RoomUserReposity roomUserRepository;
	
	@Autowired
    private RoomRoleRepository roomRoleRepository;
    
	@Autowired
    private RoomCodeRepository roomCodeRepository;
	
	@Autowired
    private RoomSpeakerRepository roomSpeakerRepository;
	
	@Autowired
    private RoomService roomService;
    
    
    public Map<String, Object> writeRoomContent(User user, int roomId, int speakerId, String content, long startTime, long endTime) {
    	
    	Room room = roomRepository.findById(roomId);
    	
    	if(room==null) return null;
    	
    	Set<RoomRole> rolesBy = roomService.getRoleRoomByUser(roomId, user.getId());
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.WRITE)) {
				RoomContent roomContent = new RoomContent();
				RoomContentReport roomContentReport = new RoomContentReport(); 
		    	
				roomContent.setContent(content);
				roomContent.setCreatedDTG(new Timestamp(new Date().getTime()));
				roomContent.setEnd(new Timestamp(endTime));
				roomContent.setStart(new Timestamp(startTime));
				roomContent.setRoomId(room.getId());
				roomContent.setSpeakerId(speakerId);
				roomContent.setUser(user);
				roomContent.setUpdatedBy(user.getId());
				roomContent.setUpdatedDTG(new Timestamp(new Date().getTime()));
		    	
				roomContent = roomContentRepository.save(roomContent);
				
				roomContentReport.setContent(content);
				roomContentReport.setCreatedDTG(new Timestamp(new Date().getTime()));
				roomContentReport.setEnd(new Timestamp(endTime));
				roomContentReport.setStart(new Timestamp(startTime));
				roomContentReport.setRoomId(room.getId());
				roomContentReport.setSpeakerId(speakerId);
				roomContentReport.setUser(user);
				roomContentReport.setUpdatedBy(user.getId());
				roomContentReport.setUpdatedDTG(new Timestamp(new Date().getTime()));
		    	
				roomContentReport = roomContentReportRepository.save(roomContentReport);
				
				
				return getRoomContent(roomContent.getId());
			}
		}
    	return null;
    	
    }
    
    private Map<String, Object> getRoomContent(int roomContentId) {
		RoomContent roomContent = roomContentRepository.findById(roomContentId);
		if(roomContent == null) {
			return null;
		}
		
		User user = roomContent.getUser();
		RoomSpeaker roomSpeaker = roomSpeakerRepository.findById(roomContent.getSpeakerId());
		
		Map<String, Object> rc = new HashMap<String, Object>();
		Map<String, Object> speaker = new HashMap<String, Object>();
		Map<String, Object> reporter = new HashMap<String, Object>();
		
		reporter.put("userId", user.getId());
		reporter.put("firstName", user.getFirstName());
		reporter.put("lastName", user.getLastName());
		reporter.put("userName", user.getUsername());
		
		speaker.put("speakerId", roomSpeaker.getId());
		speaker.put("firstName", roomSpeaker.getFirstName());
		speaker.put("lastName", roomSpeaker.getLastName());
		
		
		rc.put("speaker", speaker);
		rc.put("content", roomContent.getContent());
		rc.put("startTime", roomContent.getStart());
		rc.put("endTime", roomContent.getEnd());
		rc.put("created", roomContent.getCreatedDTG());
		rc.put("reporter", reporter);
		
		
		return rc;
		
	}
    
    public List<Map<String, Object>> getListRoomContent(int roomId, User user) {
    	List<Map<String, Object>> contents = new ArrayList<Map<String,Object>>();
    	if(roomService.checkInRoom(roomId, user)){
    		List<RoomContent> list = roomContentRepository.findByRoomId(roomId);
//    		----------------------------------
    		System.out.println("------------- Chuoi ban dau--------------------");
    		for (RoomContent roomContent : list) {
				System.out.println("SpeakerID" + roomContent.getSpeakerId() +", Content: " + roomContent.getContent() +", Time: "+ roomContent.getStart().getTime());
			}
    		
    		System.out.println("------------Chuoi sau khi sap xep ----------------");
    		ArrayList<RoomContent> lstRoomContents = new ArrayList<>(); // lst sau khi da bo cac doan transcript trung nhau;
    		Collections.sort(list);
    		for (RoomContent roomContent : list) {
				System.out.println("SpeakerID" + roomContent.getSpeakerId() +", Content: " + roomContent.getContent() +", Time: "+ roomContent.getStart().getTime());
			}
    		long L = 2; // do tre cua nguoi su dung
    		long timeMin = 4; // thoi gian it nhat de noi mot cau co nghia
    		long B = 2; // thoi gian trung binh ngat giua cac cau neu nguoi noi mot doan
    		long Break1 = timeMin + B + L;
    		
    		RoomContent contentChoose = new RoomContent();
    		contentChoose = list.get(0);
    	
    		for(int i=1; i< list.size(); i++) {
    			RoomContent contentNext = list.get(i);
    			long distance = (contentNext.getStart().getTime() - contentChoose.getStart().getTime())/1000; // khoang cach giua thoi gian bat dau cua message hien tai va message tiep theo
    			if(distance < L && contentChoose.getSpeakerId() == contentNext. getSpeakerId()) {
    				if(contentNext.getContent().length() > contentChoose.getContent().length()) {
    					contentChoose = contentNext;
    				}
    				contentChoose.setStart(new Timestamp(contentChoose.getStart().getTime() + contentNext.getStart().getTime()/2));
    				contentChoose.setEnd(new Timestamp(contentChoose.getEnd().getTime() + contentNext.getEnd().getTime()/2));
    			}
    			else if((distance >= Break1 && contentNext.getStart().getTime() > contentChoose.getEnd().getTime())
    					|| contentChoose.getSpeakerId() != contentNext.getSpeakerId() || i == (list.size() - 1)) {
    				RoomContent rContent = new RoomContent();
    				rContent.setSpeakerId(contentChoose.getSpeakerId());
    				rContent.setRoomId(contentChoose.getRoomId());
    				rContent.setContent(contentChoose.getContent());
    				rContent.setStart(contentChoose.getStart());
    				rContent.setEnd(contentChoose.getEnd());
    				lstRoomContents.add(rContent);
    				contentChoose = contentNext;
    			}
//    			if(i == (list.size() - 1)) {
//    				RoomContent rContent = new RoomContent();
//    				rContent.setContent(contentChoose.getContent());
//    				rContent.setStart(contentChoose.getStart());
//    				rContent.setEnd(contentChoose.getEnd());
//    				lstRoomContents.add(rContent);
//    			}
    		}
    		
    		// -- in list sau khi merge
    		System.out.println("------------------------------ List sau khi merge -----------------------------------------------------");
    		for (RoomContent item : lstRoomContents) {
				System.out.println("SpeakerID: "+ item.getSpeakerId() +", Content: " + item.getContent() +" , TimeStart: " + item.getStart() +" , TimeEnd: " + item.getEnd());
			}
    		
    		
    		
    		
    		
    		
//    		-------------------------------
    		for(RoomContent roomContent: list) {
    			RoomSpeaker roomSpeaker = roomSpeakerRepository.findById(roomContent.getSpeakerId());
    			
    			Map<String, Object> rc = new HashMap<String, Object>();
    			Map<String, Object> speaker = new HashMap<String, Object>();
    			Map<String, Object> reporter = new HashMap<String, Object>();
    			
    			reporter.put("userId", roomContent.getUser().getId());
    			reporter.put("firstName", roomContent.getUser().getFirstName());
    			reporter.put("lastName", roomContent.getUser().getLastName());
    			reporter.put("userName", roomContent.getUser().getUsername());
    			
    			speaker.put("speakerId", roomSpeaker.getId());
    			speaker.put("firstName", roomSpeaker.getFirstName());
    			speaker.put("lastName", roomSpeaker.getLastName());
    			
    			
    			rc.put("speaker", speaker);
    			rc.put("content", roomContent.getContent());
    			rc.put("startTime", roomContent.getStart());
    			rc.put("endTime", roomContent.getEnd());
    			rc.put("created", roomContent.getCreatedDTG());
    			rc.put("reporter", reporter);
    			
    			contents.add(rc);
    		}
    	}
    	return contents;
    }
    
    private int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
          int lastValue = i;
          for (int j = 0; j <= s2.length(); j++) {
            if (i == 0)
              costs[j] = j;
            else {
              if (j > 0) {
                int newValue = costs[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1))
                  newValue = Math.min(Math.min(newValue, lastValue),
                      costs[j]) + 1;
                costs[j - 1] = lastValue;
                lastValue = newValue;
              }
            }
          }
          if (i > 0)
            costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
      }

}