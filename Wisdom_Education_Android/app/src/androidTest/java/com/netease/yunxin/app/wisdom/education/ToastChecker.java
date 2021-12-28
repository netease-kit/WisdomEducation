package com.netease.yunxin.app.wisdom.education;

import android.os.SystemClock;

/**
 * toast监测
 */
public class ToastChecker implements IGlobalEventChecker {
    private String message = "";
    private boolean flag = false;

    @Override
    public void check(String toastMessage) {
        if (toastMessage.contains(message)) {
            flag = true;
        }
    }

    /**
     * 开始toast监测
     * @param message
     */
    public void startCheck(String message) {
        this.message = message;
        GlobalEventListener.getInstance().registerToastChecker(this);
    }

    /**
     * 等待toast出现
     * @return 2秒内监测到toast直接返回true，没有监测到返回false
     */
    public boolean waitChecked() {
        return this.waitChecked(2000);
    }

    /**
     * 等待toast出现
     * @param waitTime 指定的监测时间
     * @return 指定时间内监测到toast直接返回true，没有监测到返回false
     */
    public boolean waitChecked(int waitTime) {
        long startTime = SystemClock.uptimeMillis();
        long elapsedTime = 0;
        while (!flag && elapsedTime <= waitTime) {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            SystemClock.sleep(500);
        }
        GlobalEventListener.getInstance().removeToastChecker(this);
        return flag;
    }
}