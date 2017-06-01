package com.lipy.jotter.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipy.jotter.R;
import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.ui.listener.OnItemClickListener;
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
public class GridNoteAdapter extends RecyclerView.Adapter {

    private List<Note> notes;

    private OnItemClickListener mOnItemClickListener;

    private Context mContext;

    public GridNoteAdapter(Context context, List<Note> titles) {
        this.notes = titles;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DefaultViewHolder defaultViewHolder = (DefaultViewHolder) holder;
        defaultViewHolder.setData(notes.get(position));
        defaultViewHolder.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView content;
        TextView time;
        View moreMenu;
        OnItemClickListener mOnItemClickListener;
        ImageView thumbnail;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            RelativeLayout gridItem = (RelativeLayout) itemView.findViewById(R.id.grid_item);
            ViewGroup.LayoutParams layoutParams = gridItem.getLayoutParams();
            layoutParams.width = ScreenUtil.getScreenDispaly(mContext)[0] / 2;
            layoutParams.height = ScreenUtil.getScreenDispaly(mContext)[0] / 2;
            gridItem.setLayoutParams(layoutParams);
            itemView.findViewById(R.id.cardview).
                    setOnClickListener(this);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail_iv);
            moreMenu = itemView.findViewById(R.id.more_menu);
            moreMenu.setOnClickListener(this);
            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(Note note) {
            this.content.setText(note.getContent());
            if (StringUtils.isNotEmpty(note.getContent())) {
                if (note.getContent().length() > 20) {
                    //TODO  字数多了换成两行显示，字体缩小。
                    //this.content.setText(note.getContent());
                }
            }
            this.time.setText(mContext.getString(R.string.note_log_text, mContext.getString(R.string.create),
                    TimeUtils.getTime(note.getCreateTime())));
            Logger.INSTANCE.e("" + note.getImagePath());
            if (StringUtils.isEmpty(note.getImagePath()) || "[]".equals(note.getImagePath())) {
                this.thumbnail.setVisibility(View.INVISIBLE);
                this.content.setTextColor(mContext.getResources().getColor(R.color.gray));
                this.time.setTextColor(mContext.getResources().getColor(R.color.gray));
            } else {
                this.thumbnail.setVisibility(View.VISIBLE);
                this.content.setTextColor(mContext.getResources().getColor(R.color.white));
                this.time.setTextColor(mContext.getResources().getColor(R.color.white));
                ImageUtils.INSTANCE.showThumbnail(mContext, note.getImagePath(), this.thumbnail);
            }
        }

        @Override
        public void onClick(View v) {
            if (R.id.cardview == v.getId()) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(getAdapterPosition());
                }
            } else if (R.id.more_menu == v.getId()) {
                showMoreMenu();
            }

        }

        private void showMoreMenu() {
            PopupMenu popup = new PopupMenu(mContext, moreMenu);
            popup.getMenuInflater()
                    .inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            mOnItemClickListener.onEditClick(getAdapterPosition());
                            break;
                        case R.id.delete:
                            mOnItemClickListener.onDeleteClick(getAdapterPosition());
                            break;
                    }

                    return true;
                }
            });
            popup.show();
        }
    }

}
