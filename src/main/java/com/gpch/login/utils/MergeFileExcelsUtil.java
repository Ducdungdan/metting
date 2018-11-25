package com.gpch.login.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MergeFileExcelsUtil {
    @Autowired
    ReadFileExcelUtil readFileExcelUtil;
    public List<Vector<String>> merge(int roomId){
        List<Vector<String>> result = new ArrayList<>();
        List<Vector<String>> resultFinal = new ArrayList<>();
        List<Vector<String>> listWho = readFileExcelUtil.readWhoFile(roomId);
        List<Vector<String>> listWhat = readFileExcelUtil.readWhatFile(roomId);
        Map<String, List<String>> listF = new HashMap<>();
        for(int i = 0; i< listWhat.size(); i++){
            String f = listWhat.get(i).get(3);
            if(listF.containsKey(f)){
                List<String> newListSpeaker = getSpeaker(listWhat.get(i).get(0), listWhat.get(i).get(1), listWho);
                List<String> oldListSpeaker = listF.get(f);
                List<String> union = getUnion(newListSpeaker, oldListSpeaker);
                listF.remove(f);
                listF.put(f, union);
            }else{
                listF.put(f, getSpeaker(listWhat.get(i).get(0), listWhat.get(i).get(1), listWho));
            }
        }
        for(int i = 0; i< listWhat.size(); i++){
            listWhat.get(i).set(3, listF.get(listWhat.get(i).get(3)).get(0));
        }
        return listWhat;
    }



    public boolean checkTimeStamp(Vector<String> who, String start, String end) {
        long a = convertTimeStamp(start);
        long b = convertTimeStamp(end);
        long c = convertTimeStamp(who.get(0));
        long d = convertTimeStamp(who.get(1));
        if (c != c || b != d) {
            return false;
        }
        return true;
    }

    public List<String> getSpeaker(String start, String end, List<Vector<String>> listWho){
        List<String> listSpeaker = new ArrayList<>();
        for(int i = 0; i< listWho.size(); i++){
            if(checkTimeStamp(listWho.get(i), start, end)){
                if(checkName(listSpeaker, listWho.get(i).get(2))){
                    listSpeaker.add(listWho.get(i).get(2));
                }
            }
        }
        return listSpeaker;
    }

    public long convertTimeStamp(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    public List<String> getUnion(List<String> list1, List<String> list2){
        List<String> union = new ArrayList<>();
        for(int i = 0; i< list1.size(); i++){
            for(int j = 0; j < list2.size(); j++){
                if(list1.get(i).equals(list2.get(j))){
                    union.add(list1.get(i));
                    break;
                }
            }
        }
        return union;
    }

    public boolean checkName(List<String> list, String speaker){
        for(String name: list){
            if(list.equals(speaker)){
                return false;
            }
        }
        return true;
    }

}
