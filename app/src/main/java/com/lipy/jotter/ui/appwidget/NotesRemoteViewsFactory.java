package com.lipy.jotter.ui.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.lipy.jotter.R;
import com.lipy.jotter.dao.NoteService;
import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.TimeUtils;

import java.util.List;

/**
 * 类似于adapter，设置item展示和处理
 * Created by lipy on 2017/3/23
 */
public class NotesRemoteViewsFactory implements NotesWidgetService.RemoteViewsFactory {
    private int widgetId;
    private Context context = null;

    private List<Note> mNotes = null;

    public NotesRemoteViewsFactory(Context context, Intent intent) {
        Logger.INSTANCE.i(" --> NotesRemoteViewsFactory");
        this.context = context;
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Logger.INSTANCE.i(" --> onCreate");

    }

    @Override
    public void onDataSetChanged() {
        Logger.INSTANCE.i(" --> onDataSetChanged");
        mNotes = NoteService.loadAll();
    }

    @Override
    // 返回item对应的子View
    public RemoteViews getViewAt(int position) {
        Logger.INSTANCE.i(" --> getViewAt");
        if (mNotes.get(position) == null) {
            return null;
        }
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.app_widget_notes_item);
        Note note = mNotes.get(position);

        rv.setTextViewText(R.id.ic_notes_item_title_tv, note.getLabel());
        rv.setTextViewText(R.id.ic_notes_item_content_tv, note.getContent());
        rv.setTextViewText(R.id.ic_notes_item_data_tv, context.getString(R.string.note_log_text, context.getString(R.string.create),
                TimeUtils.getTime(note.getCreateTime())));
        loadItemOnClickExtras(rv, position);
        return rv;
    }

    @Override
    public void onDestroy() {
        Logger.INSTANCE.i(" --> onDestroy");
    }

    @Override
    public int getCount() {
        Logger.INSTANCE.i(" --> getCount");
        return mNotes.size();
    }

    /**
     * Item设置点击事件
     * @param rv
     * @param position
     */
    private void loadItemOnClickExtras(RemoteViews rv, int position) {
        /*
         * 为每一个item都设置pendingIntent是很繁琐的，需要很多很多的pendingIntent，因此不允许这样做。
		 * 之前在provider，通过setPendingIntentTemplate（）我们已经为整个remote
		 * list统一设置了item点击触发的 pendingintent模板。
		 * setOnClickFillInIntent()在模板的基础上为具体的item设置fillInIntent，
		 * fillInIntent会加入到PendingIntent模板中。
		 * 每个item触发，相同的部分在模板，不同的部分在fillInIntent
		 */
        Logger.INSTANCE.i(" --> loadItemOnClickExtras");
        Intent intent = new Intent();
        intent.putExtra(AppWidgetNotesProvider.Companion.getNOTES_ITEM_CLICK_ACTION(),
                 position);
        rv.setOnClickFillInIntent(R.id.ic_notes_item_ll, intent);
    }

    @Override
    public RemoteViews getLoadingView() {
        Logger.INSTANCE.i(" --> getLoadingView");
        return null;
    }

    @Override
    public int getViewTypeCount() {
        Logger.INSTANCE.i(" --> getViewTypeCount");
        return 1;
    }

    @Override
    public long getItemId(int position) {
        // 通过getItemId来判断那些需要getView从而达到局部刷新的效果，在getView比较耗时的情况下起到优化的效果
        Logger.INSTANCE.i(" --> getItemId");
        return position;
    }

    @Override
    public boolean hasStableIds() {
        Logger.INSTANCE.i(" --> hasStableIds");
        return true;
    }

}
