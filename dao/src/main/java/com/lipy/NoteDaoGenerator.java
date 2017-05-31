package com.lipy;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class NoteDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "greendao");
        addNote(schema);
        new DaoGenerator().generateAll(schema, "E:/TrifleNote/app/src/main/java-green");
    }

    /**
     * @param schema
     */
    private static void addNote(Schema schema) {
        Entity note = schema.addEntity("Note");
        note.addIdProperty();
        note.addIntProperty("note_id");//笔记编号
        note.addStringProperty("guid");//用户ID
        note.addIntProperty("status");//状态
        note.addIntProperty("type");//类型
        note.addStringProperty("label");//标题
        note.addStringProperty("content");//内容
        note.addStringProperty("imagePath");//图片目录
        note.addStringProperty("voicePath");//声音目录
        note.addLongProperty("createTime");//创建时间
        note.addLongProperty("lastOprTime");//最后修改时间

        Entity user = schema.addEntity("User");//创建用户表格
        user .addIdProperty();
        user.addStringProperty("userName").notNull();//用户名称
        user.addStringProperty("userpassword").notNull();//用户密码
    }
}
