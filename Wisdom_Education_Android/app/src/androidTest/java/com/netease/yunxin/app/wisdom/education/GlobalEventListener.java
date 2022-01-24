package com.netease.yunxin.app.wisdom.education;

import android.app.Instrumentation;
import android.app.Notification;
import android.app.UiAutomation;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Monitor toasts and aotomatically allow to grant permissions
 */
public class GlobalEventListener {
    private static final String TAG = "GlobalEventListener";

    private static GlobalEventListener sInstance;

    private final HashSet<IGlobalEventChecker> toastCheckerSet;

    final private String packages = "com\\.lbe\\.security\\.miui|com\\.huawei\\.systemmanager|com\\.smartisanos\\.systemui|^.*coloros.*$|^.*android.*$|com\\.miui\\.home|^.*xiaomi.*$|android|^.*meitu.*$";
    final private String allowButton = "允许|同意|始终允许|确认|总是允许|确定|仅使用时允许|仅在使用中允许";

    /**
     * Automatically allow to grant pop-up permissions
     */
    private boolean permissionsWindowPermit = false;

    private Instrumentation instrumentation;
    private UiDevice uiDevice;


    public GlobalEventListener() {
        toastCheckerSet = new HashSet<>();
    }

    public static GlobalEventListener getInstance() {
        if (sInstance == null) {
            sInstance = new GlobalEventListener();
        }
        return sInstance;
    }

    public void init(Instrumentation instrumentation, UiDevice uiDevice) {
        this.instrumentation = instrumentation;
        this.uiDevice = uiDevice;
        this.initListener();
    }


    private void initListener() {
        instrumentation.getUiAutomation().setOnAccessibilityEventListener(
                new UiAutomation.OnAccessibilityEventListener() {
                    @Override
                    public void onAccessibilityEvent(AccessibilityEvent event) {
                        try {
                            final int eventType = event.getEventType();
                            // handle pop-ups for permissions
                            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && permissionsWindowPermit) {


                                //package matches
                                final String packageName = event.getPackageName().toString();
                                if (!Pattern.matches(packages, packageName)) {
                                    return;
                                }


                                BySelector permissionsSelector = By.pkg(packageName).text(Pattern.compile(allowButton));
                                UiObject2 obj = uiDevice.findObject(permissionsSelector);
                                if (obj!= null) {

                                    //snapshot log
//                                    LogUtils.getInstance().infoScreenshot(new RectCanvasHandler(obj.getVisibleBounds()));
                                    obj.click();

                                }

                            } else if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                                //Check whether it is a notification event
                                Parcelable parcelable = event.getParcelableData();
                                //If it is not the head-up notifications, it belongs to other types, such as toast
                                if (!(parcelable instanceof Notification)) {
                                    List <CharSequence> messageList = event.getText();
                                    for (CharSequence toastMessage : messageList) {
                                        if (!TextUtils.isEmpty(toastMessage)) {
                                            Log.i(TAG, "check " + toastMessage);
                                            for (IGlobalEventChecker toastChecker : toastCheckerSet) {
                                                toastChecker.check(toastMessage.toString());
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
    }

    /**
     * Register the toast observer
     * @param toastChecker The toast observer
     */
    public void registerToastChecker(IGlobalEventChecker toastChecker) {
        toastCheckerSet.add(toastChecker);
    }

    /**
     * Remove the toast observer
     * @param toastChecker The toast observer
     */
    public void removeToastChecker(IGlobalEventChecker toastChecker) {
        if (!toastCheckerSet.isEmpty()) {
            toastCheckerSet.remove(toastChecker);
        }
    }

    /**
     * Specify whether to automatically allow to grant permissions
     * @param permit
     */
    public void usePermissionsWindowHandler(boolean permit) {
        permissionsWindowPermit = permit;
    }

}



