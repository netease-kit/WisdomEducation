package com.netease.util.gson;

import com.google.gson.FieldNamingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public enum JsonFieldNamingPolicy implements FieldNamingStrategy {
    GsonProperty() {
        @Override
        public String translateName(Field f) {
            GsonProperty annotation = f.getAnnotation(GsonProperty.class);
            if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
                return annotation.value();
            }
            return f.getName();
        }
    }
}
