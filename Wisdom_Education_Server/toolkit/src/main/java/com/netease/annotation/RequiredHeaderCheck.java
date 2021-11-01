package com.netease.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiredHeaderCheck {
    boolean requireDeviceId() default false;
    boolean requireClientType() default false;
    boolean requireCurTime() default false;
    boolean requireNonce() default false;
    boolean requireCheckSum() default false;
}
