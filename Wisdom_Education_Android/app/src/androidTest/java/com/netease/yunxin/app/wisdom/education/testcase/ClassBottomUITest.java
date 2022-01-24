package com.netease.yunxin.app.wisdom.education.testcase;

import com.netease.yunxin.integrationtest.library.uitest.ClassRegister;

import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.assertViewExist;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.clickUI;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.viewClick;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.waitForTime;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.waitForView;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.*;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.*;

/**
 * Toolbar at the bottom of the class
 */
@ClassRegister
public class ClassBottomUITest {

    public static void checkBottomBtn(String text) {
        assertViewExist(MAIN_BOTTOM_ITEM_LABEL, text);
    }

    public static void clickBottomBtn(String text) {
        waitForTime(1000);
        try {
            if(waitForView(MAIN_BOTTOM_ITEM_LABEL, text, 3000L, 1000L)) {
                viewClick(MAIN_BOTTOM_ITEM_LABEL, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Control not found. Possible harmonyOS device.
            switch (text) {
                case AUDIO_OFF:
                case AUDIO_ON:
                    viewClick(MAIN_BOTTOM_ITEM_AUDIO);
                    break;
                case VIDEO_ON:
                case VIDEO_OFF:
                    viewClick(MAIN_BOTTOM_ITEM_VIDEO);
                    break;
                case SHARE_ON:
                case SHARE_OFF:
                    viewClick(MAIN_BOTTOM_ITEM_SHARE);
                    break;
            }
        }

        waitForTime(3000);
        if(SCREEN_SHARE.equals(text)) {
            clickUI(BEGIN_IMMEDIATELY);
            clickUI(ALLOW);
        }
        waitForTime(1000);
    }

}
