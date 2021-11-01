package com.netease.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonIgnoreStrategy implements ExclusionStrategy {

    public GsonIgnoreStrategy() {
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(GsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
