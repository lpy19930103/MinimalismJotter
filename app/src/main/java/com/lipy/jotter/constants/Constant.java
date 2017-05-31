package com.lipy.jotter.constants;

/**
 * 常用常量
 * Created by lipy on 2017/3/2 0002.
 */
public class Constant {
    //笔记状态
    public final static String INTENT_NOTE_MODE = "NOTE_MODE";
    public final static String INTENT_NOTE = "NOTE";
    public final static int VIEW_NOTE_MODE = 0x00;
    /**
     * 编辑笔记
     */
    public final static int EDIT_NOTE_MODE = 0x01;
    /**
     * 创建笔记
     */
    public final static int CREATE_NOTE_MODE = 0x02;
    /**
     * 从小部件中选择录音创建笔记
     */
    public final static int CREATE_NOTE_FROM_RECODER_MODE = 0x03;
    //笔记分类类型
    public final static int NOTE_TYPE_EVERYDAY_LIFE= 1;
    public final static int NOTE_TYPE_EVERYDAY_HOBBY =2;
    public final static int NOTE_TYPE_EVERYDAY_WORK = 3;
    public final static int NOTE_TYPE_EVERYDAY_COOK = 4;

    //从小部件不同的button所启动应用的名字
    public final static String LUNCH_NAME = "LUNCH_NAME";
    public final static String LUNCH_CAMERA = "CAMREA";
    public final static String LUNCH_RECODER = "RECODER";

    //小部件的HandleMsgActivity跳转到camera的requestCode
    public static final int HANDLE_MSG_TO_LAUNCH_CAMERA= 1000;
    //NoteEditActivity跳转到camera的requestCode
    public static final int EDIT_TO_LAUNCH_CAMERA= 2000;
    //设置chronometer的计时方式
    public static final int CHRONOMETER_START = 1;
    public static final int CHRONOMETER_STOP = 2;
    public static final int CHRONOMETER_RESET = 3;
}
