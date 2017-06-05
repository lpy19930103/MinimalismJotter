package com.lipy.jotter.dao;

import com.lipy.jotter.dao.daocore.Tag;

import java.util.List;


/**
 * 标签
 * Created by lipy on 17/4/7.
 */

public class TagService {

    public static List<Tag> loadAll() {
        return DaoService.getInstance().getDaoSession().getTagDao().loadAll();
    }

    public static void deleteTag(Tag tag) {
        DaoService.getInstance().getDaoSession().getTagDao().delete(tag);
    }

    public static void saveTag(Tag tag) {
        DaoService.getInstance().getDaoSession().getTagDao().update(tag);
    }

    public static void insertTag(Tag tag) {
        DaoService.getInstance().getDaoSession().getTagDao().insert(tag);

    }
}
