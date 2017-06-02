package com.lipy.jotter.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lipy.jotter.R;
import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.ui.listener.OnNoteItemClickListener;
import com.lipy.jotter.utils.ImageUtils;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.ScreenUtil;
import com.lipy.jotter.utils.StringUtils;
import com.lipy.jotter.utils.TimeUtils;

import java.util.List;


/**
 * 卡片笔记布局
 * Created by lipy on 2016/7/22.
 */
public class GridNoteAdapter extends BaseAdapter<Note> {

    private List<Note> notes;

    private OnNoteItemClickListener mOnNoteItemClickListener;

    private Context mContext;

    public GridNoteAdapter(Context context, List<Note> notes) {
        super(R.layout.grid_item_note, notes);
        this.notes = notes;
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, Note note) {
        RelativeLayout gridItem = holder.getView(R.id.grid_item);
        ViewGroup.LayoutParams layoutParams = gridItem.getLayoutParams();
        layoutParams.width = ScreenUtil.getScreenDispaly(mContext)[0] / 2;
        layoutParams.height = ScreenUtil.getScreenDispaly(mContext)[0] / 2;
        gridItem.setLayoutParams(layoutParams);

        holder.addOnClickListener(R.id.cardview).addOnLongClickListener(R.id.cardview);
        TextView content = holder.getView(R.id.content);
        TextView time = holder.getView(R.id.time);
        ImageView thumbnail = holder.getView(R.id.thumbnail_iv);

        content.setText(note.getContent());
        if (StringUtils.isNotEmpty(note.getContent())) {
            if (note.getContent().length() > 20) {
                //TODO  字数多了换成两行显示，字体缩小。
                //this.content.setText(note.getContent());
            }
        }
        time.setText(mContext.getString(R.string.note_log_text, mContext.getString(R.string.create),
                TimeUtils.getTime(note.getCreateTime())));

        Logger.INSTANCE.e("" + note.getImagePath());
        if (StringUtils.isEmpty(note.getImagePath()) || "[]".equals(note.getImagePath())) {
            thumbnail.setVisibility(View.INVISIBLE);
            content.setTextColor(mContext.getResources().getColor(R.color.gray));
            time.setTextColor(mContext.getResources().getColor(R.color.gray));
        } else {
            thumbnail.setVisibility(View.VISIBLE);
            content.setTextColor(mContext.getResources().getColor(R.color.white));
            time.setTextColor(mContext.getResources().getColor(R.color.white));
            ImageUtils.INSTANCE.showThumbnail(mContext, note.getImagePath(), thumbnail);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (mOnNoteItemClickListener != null) {
            mOnNoteItemClickListener.onItemClick(position);
        }
    }

    @Override
    public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
        showMoreMenu(view, position);
        return false;
    }

    public void setOnItemClickListener(OnNoteItemClickListener onNoteItemClickListener) {
        this.mOnNoteItemClickListener = onNoteItemClickListener;
    }


    private void showMoreMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        mOnNoteItemClickListener.onEditClick(position);
                        break;
                    case R.id.delete:
                        mOnNoteItemClickListener.onDeleteClick(position);
                        break;
                }

                return true;
            }
        });
        popup.show();
    }

}
