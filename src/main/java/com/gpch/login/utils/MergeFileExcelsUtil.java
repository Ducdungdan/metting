package com.gpch.login.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Component
public class MergeFileExcelsUtil {
    @Autowired
    ReadFileExcelUtil readFileExcelUtil;
    public List<Vector<String>> merge(int roomId){
        List<Vector<String>> result = new ArrayList<>();
        List<Vector<String>> resultFinal = new ArrayList<>();
        List<Vector<String>> listWho = readFileExcelUtil.readWhoFile(roomId);
        List<Vector<String>> listWhat = readFileExcelUtil.readWhatFile(roomId);
        System.out.println("WHO: " + listWho.toString());
        System.out.println("WHAT: " + listWhat.toString());
        for(Vector<String> what : listWhat){
            for(Vector<String> who: listWho){
                boolean timeStartValid = Integer.parseInt(what.get(0)) == Integer.parseInt(who.get(0));
                boolean timeEndValid = Integer.parseInt(what.get(1)) == Integer.parseInt(who.get(1));
                boolean fvalid = Integer.parseInt(what.get(3)) == Integer.parseInt(who.get(3));
                if(timeEndValid && timeStartValid && fvalid){
                    Vector<String> v = new Vector<>();
                    v.add(what.get(0));
                    v.add(what.get(1));
                    v.add(who.get(2));
                    v.add(what.get(2));
                    v.add(what.get(3));
                    if(result.size() == 0){
                        result.add(v);
                    }else{
                        if(checkValid(v, result.get(result.size()-1))){
                            result.add(v);
                        }
                    }
                }
            }
        }
        System.out.println("RESULT" + result);
        int flag = 0;
        for(int i = 0; i< result.size(); i++){
            if(i < flag) break;
            int f = Integer.parseInt(result.get(i).get(4));
            for(int j = i+1; j < result.size(); j++){
                int ff = Integer.parseInt(result.get(j).get(4));
                if(f == ff){
                    String content = result.get(i).get(3);
                    content += ".";
                    content += result.get(j).get(3);
                    result.get(i).set(i, content);
                }else{
                    Vector<String> v = new Vector<>();
                    v.add(result.get(i).get(0));
                    v.add(result.get(i).get(1));
                    v.add(result.get(i).get(2));
                    v.add(result.get(i).get(3));
                    resultFinal.add(v);
                    flag = j;
                    break;
                }
            }
            if(i == result.size()-1){
                Vector<String> v = new Vector<>();
                v.add(result.get(i).get(0));
                v.add(result.get(i).get(1));
                v.add(result.get(i).get(2));
                v.add(result.get(i).get(3));
                resultFinal.add(v);
            }
        }
        return resultFinal;
    }

    public boolean checkValid(Vector<String> v1, Vector<String> v2){
        int count = 0;
        for(int i = 0; i< v1.size(); i++){
            if(v1.get(i).equals(v2.get(i))){
                count++;
            }
        }
        if(count == v1.size()){
            return false;
        }
        return true;
    }

}
