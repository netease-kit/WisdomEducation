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
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.BACK;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.CLASS_END;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.ENTER_CLASS;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.WISDOM_EDUCATION;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.AFTER_CLASS_BACK_LOGIN;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.MAIN_BACK_LOGIN;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.MAIN_DIALOG_CANCEL;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.MAIN_DIALOG_OK;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.MAIN_END_CLASS;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.MAIN_MEMBER_VIDEO;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.MAIN_START_CLASS;

/**
 * The landing page of the class
 */
@ClassRegister
public class ClassUITest {

    public static void startClass() {
        clickStartClass();
        waitForTime(1000);
        clickOk();
    }

    public static void endClass() {
        waitForTime(3000);
        clickEndClass();
        waitForTime(1000);
        clickOk();
        assertViewExist(CLASS_END);
        assertViewExist(BACK);
    }

    public static void backToLogin(int backType, int operationType) {
        waitForTime(3000);
        if (backType == 0) {
            clickBackLogin();
        } else {
            pressBack();
        }
        if (operationType == 0) {
            clickCancel();
        } else {
            clickOk();
            assertViewExist(WISDOM_EDUCATION);
            assertViewExist(ENTER_CLASS);
        }
    }

//    private static void backToLogin(int operationType) {
//        backToLogin(0, operationType);
//    }

    public static void backToLoginAfterClass() {
        clickBackLoginAfterClass();
        assertViewExist(WISDOM_EDUCATION);
        assertViewExist(ENTER_CLASS);
    }

    public static void clickStartClass() {
        viewClick(MAIN_START_CLASS);
    }

    public static void clickEndClass() {
        viewClick(MAIN_END_CLASS);
    }

    public static void clickOk() {
        viewClick(MAIN_DIALOG_OK);
    }

    public static void clickCancel() {
        viewClick(MAIN_DIALOG_CANCEL);
    }

    public static void clickBackLogin() {
        viewClick(MAIN_BACK_LOGIN);
    }

    public static void clickBackLoginAfterClass() {
        viewClick(AFTER_CLASS_BACK_LOGIN);
    }


    /**
     * Click the video UI view, check menu options and reset the view state
     * @param member The name of the member
     *
     */
    public static void checkVideoItem(String member, String menuItem) {
        clickOnRecyclerItemWithHolder(MAIN_MEMBER_VIDEO, hasTextInHolderWithPrefix(member));
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
