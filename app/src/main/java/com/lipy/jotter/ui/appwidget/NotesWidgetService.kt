package com.lipy.jotter.ui.appwidget

import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViewsService
import com.lipy.jotter.utils.Logger


/**
 * 通过后台服务（RemoteViews Service的继承）对list view的数据进行设置。
 * 整个过程Android进行了很好的封装，我们只需在onGetViewFactory()中
 * 返回一个Remote list adapter，而这个adapter的类型是RemoteViewsFactory
 * Created by lipy on 2017/3/23
 */
class NotesWidgetService : RemoteViewsService() {

    override fun onBind(intent: Intent): IBinder? {
        Logger.i(" --> onBind")
        return super.onBind(intent)
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        Logger.i(" --> onGetViewFactory")
        return NotesRemoteViewsFactory(applicationContext, intent)
    }

}