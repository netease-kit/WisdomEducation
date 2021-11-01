package com.netease.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeCachedThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(TimeCachedThread.class);
    public static volatile long currentTimeMillis = System.currentTimeMillis();
    public static volatile long currentTimeSecond = currentTimeMillis / 1000;

    public TimeCachedThread() {
        setName("TimeCacheThread-" + getId());
        setDaemon(true);
    }

    static {
        new TimeCachedThread().start();
    }

    public void run() {
        while (true) {
            currentTimeMillis = System.currentTimeMillis();
            currentTimeSecond = currentTimeMillis / 1000L;
            /*if (logger.isDebugEnabled()) {
                logger.debug("TimeCachedThread sec={}, ms={}", currentTimeSecond, currentTimeMillis);
            }*/
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
