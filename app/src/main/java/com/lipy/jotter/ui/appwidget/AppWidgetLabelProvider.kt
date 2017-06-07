package com.lipy.jotter.ui.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.lipy.jotter.R

import com.lipy.jotter.constants.Constant
import com.lipy.jotter.constants.ConstantWidget
import com.lipy.jotter.utils.Logger

/**
 * 笔记快捷操作小部件
 * created by lipy on 2017/3/20
 */
class AppWidgetLabelProvider : AppWidgetProvider() {

    private val LABEL_ACTION = "label_action"
    private val ADD_ACTION = "add_action"
    private val RECORDING_ACTION = "recording_action"
    private val RECORD_ACTION = "record_action"
    private val PICTURES_ACTION = "pictures_action"
    private val MORE_ACTION = "more_action"

    /**
     * 删除一个AppWidget时调用
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Logger.i("--> onDeleted")
        Toast.makeText(context, "删除小部件", Toast.LENGTH_SHORT).show()
        super.onDeleted(context, appWidgetIds)
    }

    /**
     * 最后一个appWidget被删除时调用
     */
    override fun onDisabled(context: Context) {
        Logger.i(" --> onDisabled")
//        Toast.makeText(context, "onDisabled", Toast.LENGTH_SHORT).show()
        super.onDisabled(context)
    }

    /**
     * AppWidget的实例第一次被创建时调用
     */
    override fun onEnabled(context: Context) {
        Logger.i(" --> onEnabled")
//        Toast.makeText(context, "onEnabled", Toast.LENGTH_SHORT).show()
        super.onEnabled(context)
    }

    /**
     * 接受广播事件
     */
    override fun onReceive(context: Context, intent: Intent) {
        Logger.i(" --> onReceive")
//        Toast.makeText(context, "onReceive", Toast.LENGTH_SHORT).show()
        if (LABEL_ACTION == intent.action) {
            Logger.i(" --> 相应Btn1按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        } else if (ADD_ACTION == intent.action) {
            Logger.i(" --> 相应Btn2按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        } else if (RECORD_ACTION == intent.action) {
            Logger.i(" --> 相应Btn3按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appIntent.putExtra(Constant.INTENT_NOTE_MODE, Constant.CREATE_NOTE_FROM_RECODER_MODE)
            context.startActivity(appIntent)
        } else if (RECORDING_ACTION == intent.action) {
            Logger.i(" --> 相应Btn4按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        } else if (PICTURES_ACTION == intent.action) {
            Logger.i(" --> 相应Btn5按钮")
            val appIntent = Intent("com.lipy.jotter.ui.activity.HandleMsgActivity")
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appIntent.putExtra(Constant.LUNCH_NAME, Constant.LUNCH_CAMERA)
            context.startActivity(appIntent)
        } else if (MORE_ACTION == intent.action) {
            Logger.i(" --> 相应Btn6按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        }

        super.onReceive(context, intent)
    }


    /**
     * 到达指定的更新时间或者当用户向桌面添加AppWidget时被调用

     * @param appWidgetManager 顾名思义是AppWidget的管理器
     * *
     * @param appWidgetIds     桌面上 所有的widget都会被分配一个唯一的ID标识，那么这个数组就是他们的列表
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        Logger.i(" --> onUpdate")
//        Toast.makeText(context, "onUpdate", Toast.LENGTH_SHORT).show()

        // 创建一个Intent对象
        val intent1 = Intent(LABEL_ACTION)
        val intent2 = Intent(ADD_ACTION)
        val intent3 = Intent(RECORD_ACTION)
        val intent4 = Intent(RECORDING_ACTION)
        val intent5 = Intent(PICTURES_ACTION)
        val intent6 = Intent(MORE_ACTION)

        // 设置pendingIntent的作用
        val pendingIntent1 = PendingIntent.getBroadcast(context, 0,
                intent1, 0)
        val pendingIntent2 = PendingIntent.getBroadcast(context, 0,
                intent2, 0)
        val pendingIntent3 = PendingIntent.getBroadcast(context, 0,
                intent3, 0)
        val pendingIntent4 = PendingIntent.getBroadcast(context, 0,
                intent4, 0)
        val pendingIntent5 = PendingIntent.getBroadcast(context, 0,
                intent5, 0)
        val pendingIntent6 = PendingIntent.getBroadcast(context, 0,
                intent6, 0)

        // 小部件在Launcher的布局
        val remoteViews = RemoteViews(context.packageName,
                R.layout.app_widget_label)

        // 绑定事件
        remoteViews.setOnClickPendingIntent(R.id.ic_widget_stars_tv, pendingIntent1)
        remoteViews.setOnClickPendingIntent(R.id.ic_note_add_text_tv, pendingIntent2)
        remoteViews.setOnClickPendingIntent(R.id.ic_record_notification_tv, pendingIntent3)
        remoteViews.setOnClickPendingIntent(R.id.ic_widget_write_tv, pendingIntent4)
        remoteViews.setOnClickPendingIntent(R.id.ic_widget_camera_tv, pendingIntent5)
        remoteViews.setOnClickPendingIntent(R.id.ic_widget_menu_more_list_tv, pendingIntent6)

        // 更新Appwidget
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }

}