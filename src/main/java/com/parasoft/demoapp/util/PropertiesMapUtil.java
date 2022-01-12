package com.parasoft.demoapp.util;

import java.util.*;

public class PropertiesMapUtil {

    public static Map<String, String> sortByKey(Map<String, String> map){
        if(map == null){
            return null;
        }
        
        HashMap<String, String> sortedMap = new LinkedHashMap<>();

        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);

        for(String key : keys){
            sortedMap.put(key, map.get(key));
        }

        return sortedMap;
    }
}
