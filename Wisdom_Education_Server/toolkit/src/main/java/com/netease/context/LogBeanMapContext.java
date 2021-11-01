package com.netease.context;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class LogBeanMapContext {
    public static final String SYNC_RESULT = "SYNC_RESULT";
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static Map<String, Object> initThread(){
        Map<String, Object> map = Maps.newLinkedHashMap();
        threadLocal.set(map);
        return map;
    }

    public static Map<String, Object> map(){
        return threadLocal.get();
    }

    public static Map<String, Object> put(String key, Object value){
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            map.put(key, value);
        }
        return map;
    }

    public static  Map<String, Object> putListItem(String key, Object item){
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            List list;
            Object o = map.get(key);
            if(!(o instanceof List)){
                list = Lists.newArrayList();
                map.put(key, list);
            }else {
                list = (List) o;
            }
            list.add(item);
        }
        return map;
    }

    public static void destroyThread(){
        threadLocal.remove();
    }
}
