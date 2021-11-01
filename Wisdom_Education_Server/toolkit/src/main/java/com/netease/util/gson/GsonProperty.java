package com.netease.util.gson;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GsonProperty {

    /**
     * the desired name of the field when it is serialized or deserialized
     */
    String value();
}
