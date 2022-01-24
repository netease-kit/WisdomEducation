package com.netease.yunxin.app.wisdom.education;

import android.os.SystemClock;

/**
 * The toast observer
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
     * Start toast observer
     * @param message
     */
    public void startCheck(String message) {
        this.message = message;
        GlobalEventListener.getInstance().registerToastChecker(this);
    }

    /**
     * Wait for toast appearing
     * @return true if toast is detected within 2 seconds, false if not detected
     */
    public boolean waitChecked() {
        return this.waitChecked(2000);
    }

    /**
     * Wait for toast appearing
     * @param waitTime The wait time
     * @return true if toast is detected within the wait time, false if not detected
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