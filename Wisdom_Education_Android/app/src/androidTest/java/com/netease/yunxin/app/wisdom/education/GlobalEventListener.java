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
 * toast监测和权限弹窗自动允许
 */
public class GlobalEventListener {
    private static final String TAG = "GlobalEventListener";

    private static GlobalEventListener sInstance;

    private final HashSet<IGlobalEventChecker> toastCheckerSet;

    final private String packages = "com\\.lbe\\.security\\.miui|com\\.huawei\\.systemmanager|com\\.smartisanos\\.systemui|^.*coloros.*$|^.*android.*$|com\\.miui\\.home|^.*xiaomi.*$|android|^.*meitu.*$";
    final private String allowButton = "允许|同意|始终允许|确认|总是允许|确定|仅使用时允许";

    /**
     * 权限弹窗自动允许
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
                            //处理权限框
                            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && permissionsWindowPermit) {


                                //package 符合
                                final String packageName = event.getPackageName().toString();
                                if (!Pattern.matches(packages, packageName)) {
                                    return;
                                }


                                BySelector permissionsSelector = By.pkg(packageName).text(Pattern.compile(allowButton));
                                UiObject2 obj = uiDevice.findObject(permissionsSelector);
                                if (obj!= null) {

                                    //截图日志
//                                    LogUtils.getInstance().infoScreenshot(new RectCanvasHandler(obj.getVisibleBounds()));
                                    obj.click();

                                }

                            } else if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                                //判断是否是通知事件
                                Parcelable parcelable = event.getParcelableData();
                                //如果不是下拉通知栏消息，则为其它通知信息，包括Toast
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
     * 注册toast监测
     * @param toastChecker toast监测器
     */
    public void registerToastChecker(IGlobalEventChecker toastChecker) {
        toastCheckerSet.add(toastChecker);
    }

    /**
     * 移除toast监测
     * @param toastChecker toast监测器
     */
    public void removeToastChecker(IGlobalEventChecker toastChecker) {
        if (!toastCheckerSet.isEmpty()) {
            toastCheckerSet.remove(toastChecker);
        }
    }

    /**
     * 是否开启权限自动允许
     * @param permit
     */
    public void usePermissionsWindowHandler(boolean permit) {
        permissionsWindowPermit = permit;
    }

}



