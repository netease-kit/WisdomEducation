package com.netease.util;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.netease.util.gson.GsonUtil;

public class Conf {

    private static final Logger logger = LoggerFactory.getLogger(Conf.class);

    private static final Properties PROPERTIES = new Properties();

    private static Throwable throwable;

    public static Throwable getThrowable() {
        return throwable;
    }

    public static Properties getProperties() {
        return PROPERTIES;
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        try {
            return Integer.valueOf(getProperties().getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public static void setInteger(String key, Integer value) {
        getProperties().setProperty(key, value.toString());
    }

    public static Long getLong(String key, Long defaultValue) {
        try {
            return Long.valueOf(getProperties().getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Long getLong(String key) {
        return getLong(key, null);
    }

    public static void setLong(String key, Long value) {
        getProperties().setProperty(key, value.toString());
    }

    public static Double getDouble(String key, Double defaultValue) {
        try {
            return Double.valueOf(getProperties().getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Double getDouble(String key) {
        return getDouble(key, null);
    }

    public static void setDouble(String key, Double value) {
        getProperties().setProperty(key, value.toString());
    }

    public static String getString(String key, String defaultValue) {
        String result = getProperties().getProperty(key);
        return result == null ? defaultValue : result;
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static void setString(String key, String value) {
        getProperties().setProperty(key, value);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        String value = getProperties().getProperty(key);
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    public static Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public static void setBoolean(String key, Boolean value) {
        getProperties().setProperty(key, value.toString());
    }

    public static class Runner implements Runnable {
        private static List<Runnable> listeners  = Lists.newArrayList();
        public Runner() {
            run();
        }

        @Override
        @Scheduled(cron = "${app.conf.reload:0 */10 * * * ?}")
        public void run() {
            try {
                File file = new ClassPathResource("conf.xml").getFile();
                Conf.getProperties().loadFromXML(new FileInputStream(file));
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("lastModified", Now.DATE_TIME.print(file.lastModified()));
                info.put("path", file.getAbsolutePath());
                logger.info(GsonUtil.toJson(info));
                throwable = null;
                for (Runnable listener : listeners) {
                    listener.run();
                }
            } catch (Exception e) {
                throwable = e;
                logger.error(e.getMessage());
            }
        }

        public static void add(Runnable runnable){
            listeners.add(runnable);
        }
    }
}