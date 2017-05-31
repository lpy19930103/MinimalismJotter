package com.lipy.jotter.ui.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.lipy.jotter.R
import com.lipy.jotter.constants.ConstantWidget
import com.lipy.jotter.utils.Logger
import com.lipy.jotter.utils.SharedPreferencesUtils

/**
 * 笔记预览小部件
 * created by lipy on 2017/3/21
 */
class AppWidgetNotesProvider : AppWidgetProvider() {

    private val SET_ACTION = "set_action"
    private val NO_NOTES_ACTION = "no_notes_action"
    private val NOTES_ADD_ACTION = "notes_add_action"
    private val ACTION_LIST_ALL_CLICK = "actioin_list_all_click"

    /**
     * 接受广播事件
     */
    override fun onReceive(context: Context, intent: Intent) {
        Logger.i("--> onReceive")
        //Toast.makeText(context, "onReceive", Toast.LENGTH_SHORT).show();
        if (NO_NOTES_ACTION == intent.action) {
            Logger.i("--> 相应Btn1按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        } else if (NOTES_ADD_ACTION == intent.action) {
            Logger.i("--> 相应Btn2按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        } else if (SET_ACTION == intent.action) {
            Logger.i("--> 相应Btn2按钮")
            val appIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        } else if (ACTION_LIST_ALL_CLICK == intent.action) {
            //点击小部件中List的item
            dealWithItemClick(context, intent)
        } else if (intent.action == COM_WIDGET_NOTES_LIST_REFRESH) {
            // 当打开应用，执行主界面的onResume时，这里会接受到Action。
            // 接受到Action时，这里应该执行一次更新Widget数据显示的操作
            updateListInfo(context)
        } else {
            super.onReceive(context, intent)
        }
    }

    /**
     * 到达指定的更新时间或者当用户向桌面添加AppWidget时被调用

     * @param appWidgetManager 顾名思义是AppWidget的管理器
     * *
     * @param appWidgetIds     桌面上 所有的widget都会被分配一个唯一的ID标识，那么这个数组就是他们的列表
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Logger.i(" --> onUpdate")
        Toast.makeText(context, "onUpdate", Toast.LENGTH_SHORT).show()

        // 创建一个Intent对象
        val intent1 = Intent(NO_NOTES_ACTION)
        val intent2 = Intent(NOTES_ADD_ACTION)
        val intent3 = Intent(SET_ACTION)
        // 设置pendingIntent的作用
        val pendingIntent1 = PendingIntent.getBroadcast(context, 0,
                intent1, 0)
        val pendingIntent2 = PendingIntent.getBroadcast(context, 0,
                intent2, 0)
        val pendingIntent3 = PendingIntent.getBroadcast(context, 0,
                intent3, 0)

        // 小部件在Launcher的布局
        val remoteViews = RemoteViews(context.packageName,
                R.layout.app_widget_notes_layout)
        // 绑定事件
        remoteViews.setOnClickPendingIntent(R.id.notes_no_ll, pendingIntent1)
        remoteViews.setOnClickPendingIntent(R.id.notes_add_write_text_tv, pendingIntent2)
        remoteViews.setOnClickPendingIntent(R.id.notes_write_text_tv, pendingIntent3)
        // 更新Appwidget的remoteViews
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)

        for (i in appWidgetIds.indices) {
            val id = appWidgetIds[i]
            //保存id，目前只有一个id
            SharedPreferencesUtils.setParam(context, "appwidgetid", id)
            updateWidget(context, appWidgetManager, id)
        }
    }

    /**
     * 更新Widget

     * @param context
     * *
     * @param appWidgetManager
     * *
     * @param widgetId
     */
    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
        Logger.i(" --> updateWidget" + widgetId)
        val rv = RemoteViews(context.packageName, R.layout.app_widget_notes_layout)
        //notes_lv为空时，显示notes_no_ll
        rv.setEmptyView(R.id.notes_lv, R.id.notes_no_ll)
        /*【1】remote list view将通过remote adpater来设置，
        remote Adapter是service，在后台对remote list item进行设置*/
        val intent = Intent(context, NotesWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        rv.setRemoteAdapter(R.id.notes_lv, intent)

        /*【2】设置点击item发送一个广播消息，
        该广播消息（action为ACTION_LIST_ALL_CLICK）将被本provider收听*/
        val onListViewClickIntent = Intent(context, AppWidgetNotesProvider::class.java)
        onListViewClickIntent.action = ACTION_LIST_ALL_CLICK
        onListViewClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

        //Log.d(tag,"uri:" + onListViewClickIntent.toUri(Intent.URI_INTENT_SCHEME));
        //onListViewClickIntent.setData(Uri.parse(onListViewClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
        // remote view的setPendingIntentTemplate()
        // 用来在remote list view中设置一个Pending Intent模板，用于响应当中item的点击
        val pi = PendingIntent.getBroadcast(
                context, 0, onListViewClickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        rv.setPendingIntentTemplate(R.id.notes_lv, pi)

        //【3】部署到具体的widget
        appWidgetManager.updateAppWidget(widgetId, rv)
    }

    /**
     * Item的点击事件

     * @param context
     * *
     * @param intent
     */
    private fun dealWithItemClick(context: Context, intent: Intent) {
        Logger.i(" --> dealWithItemClick")
        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Logger.d("无效widget ID")
            return
        }
        var itemText: String? = intent.getStringExtra(NOTES_ITEM_CLICK_ACTION)
        if (itemText == null) {
            itemText = "Error"
        }
        itemText = "You have client on item : " + itemText
        Toast.makeText(context, itemText, Toast.LENGTH_LONG).show()
        val noteIntent = Intent(ConstantWidget.NOTE_ACTIVITY_NAME)
        //传递点击的某条笔记，在NoteEditActivity中打开编辑
        noteIntent.putExtras(intent)
        noteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(noteIntent)
    }

    /**
     * 刷新ListView

     * @param context
     */
    private fun updateListInfo(context: Context) {
        val widgetId = SharedPreferencesUtils.getParam(context, "appwidgetid", AppWidgetManager.INVALID_APPWIDGET_ID) as Int
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Logger.d("-->无效widget ID" + widgetId)
            return
        }
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.notes_lv)
    }

    companion object {
        val NOTES_ITEM_CLICK_ACTION = "notes_item_click_action"
        val COM_WIDGET_NOTES_LIST_REFRESH = "com.widget.notes.list.refresh"
    }
}