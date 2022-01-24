package com.netease.yunxin.app.wisdom.education;

/**
 * IDs used in Wisdom Education
 */
public final class NEEduTestConstants {

    /**
     * view ID list
     */
    public interface ViewIds {
        int LOGIN_ROOM_ID = R.id.et_room_id;
        int LOGIN_NICK_NAME = R.id.et_nick_name;
        int LOGIN_SCENE_TYPE = R.id.et_scene_type;
        int LOGIN_TYPE_TEACHER = R.id.rb_teacher;
        int LOGIN_TYPE_STUDENT = R.id.rb_student;
        int LOGIN_JOIN = R.id.btn_join;


        int MAIN_START_CLASS = R.id.btn_clazz_ctrl_right;
        int MAIN_END_CLASS = R.id.btn_clazz_ctrl_right;
        int MAIN_DIALOG_OK = R.id.ok;
        int MAIN_DIALOG_CANCEL = R.id.cancel;
        int MAIN_BACK_LOGIN = R.id.tv_back;
        int AFTER_CLASS_BACK_LOGIN = R.id.btn_class_finish_back;

        int MAIN_BOTTOM_ITEM_LABEL = R.id.tv_bottom_label;

        int MAIN_BOTTOM_ITEM_AUDIO = R.id.item_audio;
        int MAIN_BOTTOM_ITEM_VIDEO = R.id.item_video;
        int MAIN_BOTTOM_ITEM_SHARE = R.id.item_share;

        int MAIN_MEMBER_VIDEO = R.id.rcv_member_video;

        int CLASS_MEMBER_VIDEO_LIST = R.id.rcv_member_video;

        int CLASS_MEMBER_VIDEO_MENU_ITEM = R.id.action_text;
    }

    /**
     * Text
     */
    public interface TextContent {
        String CLASS_NOT_START = "课堂未开始";
        String MUTE = "静音";
        String SCREEN_SHARE = "共享屏幕";

        String CLASS_END = "课程已结束";
        String BACK = "返回";
        String WISDOM_EDUCATION = "智慧云课堂";
        String ENTER_CLASS = "加入课堂";

        String AUDIO_OFF = "静音";
        String AUDIO_ON = "解除静音";
        String VIDEO_ON = "开启视频";
        String VIDEO_OFF = "关闭视频";
        String SHARE_ON = "共享屏幕";
        String SHARE_OFF = "停止共享";

        String BEGIN_IMMEDIATELY = "立即开始";
        String ALLOW = "允许";
    }

    /**
     * IDs of image resources
     */
    public interface DrawableRes {

    }

}
