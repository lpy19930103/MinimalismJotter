package com.lipy.jotter.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lipy.jotter.R;
import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.ui.listener.OnNoteItemClickListener;
import com.lipy.jotter.utils.ImageUtils;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.StringUtils;
import com.lipy.jotter.utils.TimeUtils;

import java.util.List;


/**
 * 笔记布局
 * Created by lipy on 2016/7/22.
 */
public class NoteAdapter extends BaseAdapter<Note> {

    private OnNoteItemClickListener mOnItemClickListener;

    private Context mContext;

    public NoteAdapter(Context context, List<Note> titles) {
        super(R.layout.item, titles);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, Note note) {
        holder.setText(R.id.title, note.getLabel())
                .setText(R.id.content, note.getContent())
                .setText(R.id.time, mContext.getString(R.string.note_log_text, mContext.getString(R.string.create),
                        TimeUtils.getTime(note.getCreateTime())))
                .addOnClickListener(R.id.cardview)
                .addOnLongClickListener(R.id.cardview);
        if (StringUtils.isEmpty(note.getImagePath()) || "[]".equals(note.getImagePath())) {
            holder.setVisible(R.id.small_pic, false);
        } else {
            holder.setVisible(R.id.small_pic, true);
            ImageUtils.INSTANCE.showThumbnail(mContext, note.getImagePath(), (ImageView) holder.getView(R.id.small_pic));
        }

    }

    public void setOnItemClickListener(OnNoteItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(position);
        }
    }

    @Override
    public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
        showMoreMenu(view, position);
        return true;
    }

    private void showMoreMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        mOnItemClickListener.onEditClick(position);
                        break;
                    case R.id.delete:
                        mOnItemClickListener.onDeleteClick(position);
                        break;
                }

                return true;
            }
        });
        popup.show();
    }
}

