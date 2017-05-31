package com.lipy.jotter.utils;

/**
 * 字符串处理工具类
 * Created by lipy on 2017/3/2.
 */
public class StringUtils {
    public static boolean isEmpty(String var0) {
        return var0 == null || var0.trim().length() == 0;
    }

    public static boolean isNotEmpty(String var0) {
        return var0 != null && var0.trim().length() > 0;
    }

    public static String getFileName(String path){
        int start = path.lastIndexOf("/");
        if(start != -1) {
            return path.substring(start + 1);
        }
        else {
            return null;
        }

    }
}
