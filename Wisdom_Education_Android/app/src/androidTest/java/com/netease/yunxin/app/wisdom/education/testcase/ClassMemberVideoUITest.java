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
 * UI view of class members
 */
@ClassRegister
public class ClassMemberVideoUITest {

    /**
     * Click the video UI view，check options of the video UI view menu and reset the view state
     * @param member The name of the member
     * @param menuText The menu text of the video UI view option
     */
    public static void checkVideoMenu(String member, String menuText) {
        clickOnRecyclerItemWithHolder(CLASS_MEMBER_VIDEO_LIST, hasTextInHolderWithPrefix(member)); // Click video UI view and open the menu.
        waitForTime(1000);

        // Check the options of the video UI view
        assertViewExist(CLASS_MEMBER_VIDEO_MENU_ITEM, menuText);
        pressBack();

        waitForTime(1000);

    }

    /**
     * Click the video UI view and click the options
     * @param member The name of the member
     * @param menuText The menu text of the video UI view option
     */
    public static void clickVideoMenu(String member, String menuText) {
        clickOnRecyclerItemWithHolder(CLASS_MEMBER_VIDEO_LIST, hasTextInHolderWithPrefix(member)); // Click the video menu view to open the menu
        waitForTime(1000);

        // Click the options of the video UI view
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
