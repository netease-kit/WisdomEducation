package com.netease.yunxin.app.wisdom.education.testcase;

import com.netease.yunxin.integrationtest.library.uitest.ClassRegister;

import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.assertViewExist;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.clickUI;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.viewClick;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.waitForTime;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.BEGIN_IMMEDIATELY;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.SCREEN_SHARE;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.MAIN_BOTTOM_ITEM_LABEL;

/**
 * 课堂底部工具栏
 */
@ClassRegister
public class ClassBottomUITest {

    public static void checkBottomBtn(String text) {
        assertViewExist(MAIN_BOTTOM_ITEM_LABEL, text);
    }

    public static void clickBottomBtn(String text) {
        waitForTime(1000);
        assertViewExist(MAIN_BOTTOM_ITEM_LABEL, text);
        viewClick(MAIN_BOTTOM_ITEM_LABEL, text);
        waitForTime(3000);
        if(SCREEN_SHARE.equals(text)) {
            clickUI(BEGIN_IMMEDIATELY);
        }
        waitForTime(1000);
    }

}
