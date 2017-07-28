package com.lipy.jotter.app

import android.app.Application
import com.lipy.jotter.dao.DaoService
import com.youdao.note.sdk.openapi.IYNoteAPI
import com.youdao.note.sdk.openapi.YNoteAPIFactory

/**
 * Application
 * Created by lipy on 2017/2/28 0028.
 */
class App : Application() {

    var AppId = ""
     var api :IYNoteAPI? = null

    override fun onCreate() {
        super.onCreate()
        if (instances == null) {
            instances = this
        }
        DaoService.getInstance().initDb(this)
//        [1]利用申请到的 AppId 生成 api 接口类
         api = YNoteAPIFactory.getYNoteAPI(this, AppId)
//        [2]注册 App 到有道云笔记
        if(!api!!.isRegistered()){ api!!.registerApp();
        }
    }

    companion object {
        var instances: App? = null
    }

}
