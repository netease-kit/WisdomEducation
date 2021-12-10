package com.netease.yunxin.app.wisdom.education.testcase;

import com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter.MemberVideoListAdapter;
import com.netease.yunxin.integrationtest.library.uitest.ClassRegister;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.assertViewExist;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.clickOnRecyclerItemWithHolder;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.pressBack;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.viewClick;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.waitForTime;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.CLASS_MEMBER_VIDEO_LIST;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.CLASS_MEMBER_VIDEO_MENU_ITEM;

/**
 * 课堂成员视频窗口
 */
@ClassRegister
public class ClassMemberVideoUITest {

    /**
     * 点击视频窗口，检查成员视频窗口选项，并复位窗口状态
     * @param member 点击视频窗口的成员名称
     * @param menuText 成员视频窗口选项文案
     */
    public static void checkVideoMenu(String member, String menuText) {
        clickOnRecyclerItemWithHolder(CLASS_MEMBER_VIDEO_LIST, hasTextInHolderWithPrefix(member)); // 点击视频窗口，打开菜单
        waitForTime(1000);

        // 检查成员视频窗口选项
        assertViewExist(CLASS_MEMBER_VIDEO_MENU_ITEM, menuText);
        pressBack();

        waitForTime(1000);

    }

    /**
     * 点击视频窗口，点击成员视频窗口选项
     * @param member 点击视频窗口的成员名称
     * @param menuText 成员视频窗口选项文案
     */
    public static void clickVideoMenu(String member, String menuText) {
        clickOnRecyclerItemWithHolder(CLASS_MEMBER_VIDEO_LIST, hasTextInHolderWithPrefix(member)); // 点击视频窗口，打开菜单
        waitForTime(1000);

        // 点击成员视频窗口选项
        assertViewExist(CLASS_MEMBER_VIDEO_MENU_ITEM, menuText);
        viewClick(CLASS_MEMBER_VIDEO_MENU_ITEM, menuText);

        waitForTime(1000);
    }

    //------------------ self matcher method
    public static Matcher<MemberVideoListAdapter.MemberViewHolder> hasTextInHolderWithPrefix(String text) {
        return new TypeSafeMatcher<MemberVideoListAdapter.MemberViewHolder>() {
            @Override
            protected boolean matchesSafely(MemberVideoListAdapter.MemberViewHolder customHolder) {
                return (customHolder.getTvName()).getText().toString().startsWith(text);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("equals with text");
            }
        };
    }

}
