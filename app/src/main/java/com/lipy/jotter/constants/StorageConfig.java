package com.lipy.jotter.constants;

import android.content.Context;
import android.os.Environment;

import com.lipy.jotter.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 所有本地存储路径配置类，应用中需要存储数据时，从此类中获取路径、文件名称、配置
 * Created by lipy on 17/4/11.
 */

public class StorageConfig {

    private static final User user = new User();


    private static String rootPath = Environment.getExternalStorageDirectory() + "/jotter";
    private static String picPath = "/pic";
    private static String voicePath = "/voice";
    private static File rootDir = null;

    /**
     * 获取本地数据库名称
     * @return 本地数据库名称
     */
    public static String getDbName() {
        return "notes-db" + user.name;
    }

    /**
     * 获取本地存储的图片路径
     * @param context 上下文
     * @return  本地存储的图片路径
     */
    public static String getImagePath(Context context) {
        rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        return rootPath + picPath + "/" + user.name;
    }

    /**
     * 获取本地存储的录音路径
     * @param context 上下文
     * @return  本地存储的录音路径
     */
    public static String getVoicePath(Context context) {
        rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        return rootPath + voicePath + "/" + user.name;
    }

    /**
     * 创建照片存放目录
     * @return
     */
    public static File creatPicFile(Context context) {
        File picDir = new File(getImagePath(context));
        if (!picDir.exists()) {
            picDir.mkdir();
        }

        String date = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(System.currentTimeMillis());
        File picPath = new File(picDir, "IMG_" + date + ".jpg");
        Logger.INSTANCE.i("StorageConfig---imageUri: " + picPath);
        if (picPath.exists()) {
            picPath.delete();
        }

        try {
            picPath.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return picPath;
    }

    /**
     * 创建录音存放目录
     * @return
     */
    public static File creatVoiceFile(Context context) {
        File voiceDir = new File(getVoicePath(context));
        if (!voiceDir.exists()) {
            voiceDir.mkdir();
        }

        String date = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(System.currentTimeMillis());
        File voicePath = new File(voiceDir, "Voice_" + date + ".3gpp");
        Logger.INSTANCE.i("voicePath: " + voicePath);
        if (voicePath.exists()) {
            voicePath.delete();
        }

        try {
            voicePath.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return voicePath;
    }

    /**
     * 临时内部类，为StorageConfig提供假的User信息，以后增加用户系统后，替换此类
     */
    private static class User {
        public String name = "";
    }
}
