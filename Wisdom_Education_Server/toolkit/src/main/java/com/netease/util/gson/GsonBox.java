package com.netease.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public enum GsonBox {
    DEFAULT(new GsonBuilder()
            .setExclusionStrategies(new GsonIgnoreStrategy())
            .setFieldNamingStrategy(JsonFieldNamingPolicy.GsonProperty)
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter())
            .create()),
    OPEN(new GsonBuilder()
            .setExclusionStrategies(new GsonIgnoreStrategy())
            .setFieldNamingStrategy(JsonFieldNamingPolicy.GsonProperty)
            .registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter())
            .create()),
    NO_DOUBLE(new GsonBuilder()
            .setExclusionStrategies(new GsonIgnoreStrategy())
            .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                if (src == src.longValue()) {
                    return new JsonPrimitive(src.longValue());
                } else {
                    return new JsonPrimitive(src);
                }
            }).create());

    private Gson gson;

    GsonBox(Gson gson) {
        this.gson = gson;
    }

    public Gson gson() {
        return gson;
    }
}
