package com.netease.util.gson;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GsonUtil {

    // -------------------------
    // JSON ELEMENT OPERATIONS
    // -------------------------

    private static final JsonParser PARSER = new JsonParser();

    private GsonUtil() {
    }

    public static JsonParser getParser() {
        return PARSER;
    }

    public static String getString(JsonObject jo, String key) {
        return getString(jo, key, null);
    }

    public static String getString(JsonObject jo, String key, String dv) {
        return (jo.has(key) && !jo.get(key).isJsonNull()) ? getAsString(jo, key).trim() : dv;
    }

    private static String getAsString(JsonObject jo, String key){
        JsonElement je = jo.get(key);
        if(je.isJsonObject() || je.isJsonArray()){
            return je.toString();
        }else{
            return je.getAsString();
        }
    }

    public static Long getLong(JsonObject jo, String key) {
        return getLong(jo, key, null);
    }

    public static Long getLong(JsonObject jo, String key, Long dv) {
        return (jo.has(key) && !jo.get(key).isJsonNull()) ? (Long) jo.get(key).getAsLong() : dv;
    }

    public static Integer getInt(JsonObject jo, String key) {
        return getInt(jo, key, null);
    }

    public static Integer getInt(JsonObject jo, String key, Integer dv) {
        return (jo.has(key) && !jo.get(key).isJsonNull()) ? (Integer) jo.get(key).getAsInt() : dv;
    }

    public static Double getDouble(JsonObject jo, String key) {
        return getDouble(jo, key, null);
    }

    public static Double getDouble(JsonObject jo, String key, Double dv) {
        return (jo.has(key) && !jo.get(key).isJsonNull()) ? (Double) jo.get(key).getAsDouble() : dv;
    }

    public static Boolean getBoolean(JsonObject jo, String key) {
        return getBoolean(jo, key, null);
    }

    public static Boolean getBoolean(JsonObject jo, String key, Boolean dv) {
        return (jo.has(key) && !jo.get(key).isJsonNull()) ? (Boolean) jo.get(key).getAsBoolean() : dv;
    }

    public static Float getFloat(JsonObject jo, String key) {
        return getFloat(jo, key, null);
    }

    public static Float getFloat(JsonObject jo, String key, Float dv) {
        return (jo.has(key) && !jo.get(key).isJsonNull()) ? (Float) jo.get(key).getAsFloat() : dv;
    }

    public static <T> T getBean(JsonObject jo, String key, Class<T> clazz) {
        if (!jo.has(key) || jo.get(key).isJsonNull()) {
            return null;
        }
        return GsonUtil.fromJson(jo.get(key), clazz);
    }

    public static Boolean getBooleanCheck(JsonObject jo, String key) {
        if(jo.has(key) && !jo.get(key).isJsonNull()){
            JsonElement je = jo.get(key);
            if(je.isJsonPrimitive()){
                JsonPrimitive jp = je.getAsJsonPrimitive();
                if(jp.isBoolean()){
                    return jp.getAsBoolean();
                }else {
                    String str = jp.getAsString();
                    if(str.equals("true")){
                        return true;
                    }else if(str.equals("false")){
                        return false;
                    }
                }
            }
        }
        return null;
    }

    public static JsonObject getJsonObject(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonObject()) {
            return je.getAsJsonObject();
        }
        return null;
    }

    public static JsonArray getJsonArray(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonArray()) {
            return je.getAsJsonArray();
        }
        return null;
    }

    public static List<String> getStringList(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            List<String> list = new ArrayList<>();
            for (JsonElement jsonElement : ja) {
                list.add(jsonElement.getAsString());
            }
            return list;
        }

        return null;
    }

    public static List<Long> getLongList(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            List<Long> list = new ArrayList<>();
            for (JsonElement jsonElement : ja) {
                list.add(jsonElement.getAsLong());
            }
            return list;
        }

        return null;
    }

    public static <T> List<T> getList(JsonObject jo, String key, Class<T> clazz) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            List<T> list = new ArrayList<>();
            for (JsonElement jsonElement : ja) {
                list.add(GsonUtil.fromJson(jsonElement, clazz));
            }
            return list;
        }

        return null;
    }



    public static Set<Long> getLongSet(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            Set<Long> list = Sets.newHashSet();
            for (JsonElement jsonElement : ja) {
                list.add(jsonElement.getAsLong());
            }
            return list;
        }

        return null;
    }

    public static Map<Long, String> getLongStringMap(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonObject()) {
            Map<Long, String> longStringMap = Maps.newHashMap();
            JsonObject mapJo = je.getAsJsonObject();
            for (String k : mapJo.keySet()) {
                longStringMap.put(Long.valueOf(k), mapJo.get(k).getAsString());
            }
            return longStringMap;
        }

        return null;
    }

    public static Set<String> getStringSet(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            Set<String> set = Sets.newLinkedHashSet();
            for (JsonElement jsonElement : ja) {
                set.add(jsonElement.getAsString().trim());
            }
            return set;
        }

        return null;
    }

    public static Map<String, String> getStringStringMap(JsonObject jo, String key) {
        JsonElement je = jo.get(key);
        if (je != null && je.isJsonObject()) {
            JsonObject jjo = je.getAsJsonObject();
            Map<String, String> map = Maps.newHashMap();
            for (String k : jjo.keySet()) {
                map.put(k, jjo.get(k).getAsString());
            }
            return map;
        }

        return null;
    }

    public static JsonElement parseJsonElement(String str) {
        return str == null ? null : getParser().parse(str);
    }

    public static JsonObject parseJsonObjectIfNotNull(String src) {
        return src == null ? null : (StringUtils.isBlank(src) ? new JsonObject() : getParser().parse(src).getAsJsonObject());
    }
    public static JsonObject parseJsonObject(String src) {
        return StringUtils.isBlank(src) ? new JsonObject() : getParser().parse(src).getAsJsonObject();
    }

    public static Map<String, String> parseStringStringMap(String s){
        JsonObject jo = GsonUtil.parseJsonObject(s);
        Map<String, String> map = Maps.newHashMap();
        for (String k : jo.keySet()) {
            map.put(k, jo.get(k).getAsString());
        }
        return map;
    }

    public static JsonObject toJsonObject(Object obj) {
        return parseJsonObject(toJson(obj));
    }

    public static JsonArray parseJsonArray(String src) {
        return StringUtils.isBlank(src) ? new JsonArray() : getParser().parse(src).getAsJsonArray();
    }

    public static JsonObject copyJsonObject(JsonObject src) {
        JsonObject ret = new JsonObject();
        Set<Map.Entry<String, JsonElement>> entries = src.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            ret.add(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    public static void copyJsonObject(JsonObject src, JsonObject ret) {
        Set<Map.Entry<String, JsonElement>> entries = src.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            ret.add(entry.getKey(), entry.getValue());
        }
    }

    public static void cutJsonObject(JsonObject src, JsonObject ret) {
        Set<Map.Entry<String, JsonElement>> entries = src.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            ret.remove(entry.getKey());
        }
    }

    public static JsonObject mergeJsonObject(JsonObject... jos) {
        JsonObject ret = new JsonObject();
        for (JsonObject jo : jos) {
            Set<Map.Entry<String, JsonElement>> entries = jo.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                ret.add(entry.getKey(), entry.getValue());
            }
        }
        return ret;
    }

    // -------------------------
    // GSON BASE DELEGATION
    // -------------------------

    public static Gson get() {
        return GsonBox.DEFAULT.gson();
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return get().fromJson(str, clazz);
    }

    public static <T> T fromJsonNoDouble(String str, Class<T> clazz){
        return GsonBox.NO_DOUBLE.gson().fromJson(str, clazz);
    }

    public static <T> T fromJson(JsonElement json, Class<T> clazz) {
        return get().fromJson(json, clazz);
    }

    public static <T> T fromJson(String str, Type type) {
        return get().fromJson(str, type);
    }

    public static <T> T fromJson(JsonElement json, Type type) {
        return get().fromJson(json, type);
    }
    public static <T> T fromJsonNoDouble(JsonElement json, Type type) {
        return GsonBox.NO_DOUBLE.gson().fromJson(json, type);
    }
    public static <T> T fromJsonNoDouble(String str, Type type) {
        return GsonBox.NO_DOUBLE.gson().fromJson(str, type);
    }
    public static String toJson(Object obj) {
        return get().toJson(obj);
    }
    public static String toJsonNoDouble(Object obj) {
        return GsonBox.NO_DOUBLE.gson().toJson(obj);
    }

    public static JsonElement toJsonTree(Object obj) {
        return get().toJsonTree(obj);
    }

}
