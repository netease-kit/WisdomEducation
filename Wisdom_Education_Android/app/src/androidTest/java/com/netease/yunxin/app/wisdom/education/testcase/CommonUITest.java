package com.netease.yunxin.app.wisdom.education.testcase;

import com.netease.yunxin.integrationtest.library.uitest.ClassRegister;

import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.assertViewExist;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.viewClick;

/**
 * Common UI testing tool class
 */
@ClassRegister
public class CommonUITest {

    /**
     * Check whether the control exists
     * @param text UI control text. Make sure the text of the control is unique. Otherwise, the specified control cannot be found.
     */
    public static void checkComponent(String text) {
        assertViewExist(text);
    }

    /**
     * Click control
     * @param text UI control text. Make sure the text of the control is unique. Otherwise, the specified control cannot be found.
     */
    public static void clickComponent(String text) {
        assertViewExist(text);
        viewClick(text);
    }

}
