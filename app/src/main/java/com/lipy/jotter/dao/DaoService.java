package com.lipy.jotter.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lipy.jotter.constants.StorageConfig;
import com.lipy.jotter.dao.daocore.DaoMaster;
import com.lipy.jotter.dao.daocore.DaoSession;


/**
 * 本地数据库service类，初始化db，并提供全局dao
 * Created by lipy on 17/4/11.
 */

public class DaoService {
    private DaoSession mDaoSession;
    private static DaoService instance = new DaoService();

    private DaoService() {

    }

    public static DaoService getInstance() {
        return instance;
    }

    /**
     * 设置greenDao
     */
    public void initDb(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, StorageConfig.getDbName(), null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    DaoSession getDaoSession() {
        return mDaoSession;
    }

}
