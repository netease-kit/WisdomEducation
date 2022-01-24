package com.netease.yunxin.app.wisdom.education;

import android.app.Instrumentation;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.netease.yunxin.app.wisdom.education.testcase.ClassBottomUITest;
import com.netease.yunxin.app.wisdom.education.testcase.ClassMemberVideoUITest;
import com.netease.yunxin.app.wisdom.education.testcase.ClassUITest;
import com.netease.yunxin.app.wisdom.education.testcase.CommonUITest;
import com.netease.yunxin.app.wisdom.education.testcase.LoginUITest;
import com.netease.yunxin.app.wisdom.education.ui.MainActivity;
import com.netease.yunxin.integrationtest.library.IntegrationTest;
import com.netease.yunxin.integrationtest.library.uitest.UITestParser;
import com.netease.yunxin.integrationtest.library.uitest.UITestRegisterHelper;
import com.netease.yunxin.kit.alog.ALog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Testlauncher {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);
    // 1. Use a mobile phone for local testing
//    private IIntegrationTest integrationTest = LocalIntegrationTest.getInstance();
    // 2. Use server instance for testing
    private IntegrationTest integrationTest = IntegrationTest.getInstance();

    @Before
    public void init() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice uiDevice = UiDevice.getInstance(instrumentation);
        UITestRegisterHelper.setDevice(uiDevice);
        GlobalEventListener globalEventListener = GlobalEventListener.getInstance();
        globalEventListener.init(instrumentation, uiDevice);
        globalEventListener.usePermissionsWindowHandler(true);
        integrationTest.setContext(ApplicationProvider.getApplicationContext());
        integrationTest.setRequestInfo("education",  "1.6.0", "zhengjinang/education");
        integrationTest.registerHandle(new UITestParser());
//        ((LocalIntegrationTest)integrationTest).setFilePath("/sdcard/Download/Education1_6_case.txt"); // Set the path of mobile local testing example file
    }

    @Test
    public void test() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        UITestRegisterHelper.registerClass(LoginUITest.class, ClassUITest.class, ClassBottomUITest.class, CommonUITest.class, ClassMemberVideoUITest.class);
        UITestRegisterHelper.setParamsAllInFirst(true);
        integrationTest.start();
        UITestRegisterHelper.releaseAll();
        ALog.w("end----------");
    }

}