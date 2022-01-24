package com.netease.yunxin.app.wisdom.education.testcase;

import com.netease.yunxin.app.wisdom.education.BaseTestUtils;
import com.netease.yunxin.integrationtest.library.uitest.ClassRegister;

import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.assertViewExist;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.setText;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.viewClick;
import static com.netease.yunxin.app.wisdom.education.BaseTestUtils.waitForTime;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.CLASS_NOT_START;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.MUTE;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.TextContent.SCREEN_SHARE;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.LOGIN_JOIN;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.LOGIN_NICK_NAME;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.LOGIN_ROOM_ID;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.LOGIN_SCENE_TYPE;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.LOGIN_TYPE_STUDENT;
import static com.netease.yunxin.app.wisdom.education.NEEduTestConstants.ViewIds.LOGIN_TYPE_TEACHER;

/**
 * Login page
 */
@ClassRegister
public class LoginUITest {

    public static void enterClass(String lessonId, String nickName, String classType, int role) {
        inputLessonId(lessonId);
        waitForTime(1000);
        inputNickName(nickName);
        waitForTime(1000);
        clickClassType();
        inputClassType(classType);
        chooseRoleType(role);
        clickJoin();
    }

    public static void inputLessonId(String lessonId) {
        BaseTestUtils.assertViewExist(LOGIN_ROOM_ID); // In case the UI component is not found
        setText(LOGIN_ROOM_ID, lessonId);
    }

    public static void inputNickName(String nickName) {
        setText(LOGIN_NICK_NAME, nickName);
    }

    public static void clickClassType() {
        viewClick(LOGIN_SCENE_TYPE);
    }

    public static void inputClassType(String classType) {
        waitForTime(1000);
        assertViewExist(classType); // the UI component is not found
        viewClick(classType);
        waitForTime(1000);
    }

    public static void chooseRoleType(int role) {
        BaseTestUtils.assertViewExist(LOGIN_TYPE_TEACHER); // the UI component is not found
        BaseTestUtils.assertViewExist(LOGIN_TYPE_STUDENT);
        if (role == 0) {
            viewClick(LOGIN_TYPE_TEACHER);
        } else {
            viewClick(LOGIN_TYPE_STUDENT);
        }
    }

    public static void clickJoin() {
        viewClick(LOGIN_JOIN);
        waitForTime(3000);
//        assertViewExist(CLASS_NOT_START);
        assertViewExist(MUTE);
        assertViewExist(SCREEN_SHARE);
    }

}
