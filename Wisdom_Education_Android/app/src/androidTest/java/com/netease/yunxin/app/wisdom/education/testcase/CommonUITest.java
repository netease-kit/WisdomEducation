package com.netease.yunxin.app.wisdom.education.testcase;

import com.netease.yunxin.integrationtest.library.uitest.ClassRegister;

import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.assertViewExist;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.viewClick;

/**
 * 通用UI测试工具类
 */
@ClassRegister
public class CommonUITest {

    /**
     * 查看控件是否存在
     * @param text UI控件文案。需要确保界面上只有一个唯一的text文案的控件。否则不一定能查找到对应控件。
     */
    public static void checkComponent(String text) {
        assertViewExist(text);
    }

    /**
     * 点击控件
     * @param text UI控件文案。需要确保界面上只有一个唯一的text文案的控件。否则不一定能查找到对应控件。
     */
    public static void clickComponent(String text) {
        assertViewExist(text);
        viewClick(text);
    }

}
