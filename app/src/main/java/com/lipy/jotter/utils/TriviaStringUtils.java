package com.lipy.jotter.utils;


import com.lipy.jotter.constants.Constant;

/**
 * 业务字符串处理工具类
 * Created by lipy on 2017/4/1
 */
public class TriviaStringUtils {

    public static String getNoteTypeTitle(int type) {
        switch (type) {
            case Constant.NOTE_TYPE_EVERYDAY_LIFE :
                return "日常生活";
            case Constant.NOTE_TYPE_EVERYDAY_HOBBY :
                return "爱好";
            case Constant.NOTE_TYPE_EVERYDAY_WORK :
                return "工作";
            case Constant.NOTE_TYPE_EVERYDAY_COOK :
                return "美食";
        }
        return "";
    }

    public static String getNoteTypeContent(Integer type) {
        switch (type) {
            case Constant.NOTE_TYPE_EVERYDAY_LIFE :
                return "让生活更有序";
            case Constant.NOTE_TYPE_EVERYDAY_HOBBY :
                return "精神愉悦之旅";
            case Constant.NOTE_TYPE_EVERYDAY_WORK :
                return "工作充电池";
            case Constant.NOTE_TYPE_EVERYDAY_COOK :
                return "让味蕾享受吧";
        }
        return "";
    }
}
