package com.lipy.jotter.app

import android.app.Application
import com.lipy.jotter.dao.DaoService

/**
 * Application
 * Created by lipy on 2017/2/28 0028.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (instances == null) {
            instances = this
        }
        DaoService.getInstance().initDb(this)
    }

    companion object {
        var instances: App? = null
    }

}
