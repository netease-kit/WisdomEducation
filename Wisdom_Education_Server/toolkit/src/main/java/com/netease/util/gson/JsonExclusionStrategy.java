package com.netease.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.lang.annotation.Annotation;
import java.util.Collection;

public class JsonExclusionStrategy implements ExclusionStrategy {
    private Collection<Class<? extends Annotation>> annotations;

    public JsonExclusionStrategy(Collection<Class<? extends Annotation>> annotations) {
        this.annotations = annotations;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        for (Class<? extends Annotation> annotation : annotations) {
            if (f.getAnnotation(annotation) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
