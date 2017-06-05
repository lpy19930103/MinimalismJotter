package com.lipy.jotter.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lipy.jotter.R;
import com.lipy.jotter.dao.daocore.Tag;

import java.util.List;


/**
 * 笔记快捷操作小部件
 * created by lipy on 2017/4/20
 */
public class ToggleMenuAdapter extends BaseAdapter{
    private Context mContext;
    private List<Tag> mNoteList;

    public ToggleMenuAdapter(Context context, List<Tag> noteList) {
        mContext = context;
        mNoteList = noteList;
    }

    @Override
    public int getCount() {
        return mNoteList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNoteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  convertView;
        ViewHolder viewHolder ;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_menu_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(mNoteList.get(position).getTag());
        viewHolder.content.setText("1");
        return view;
    }
    class ViewHolder{
        private TextView title;
        private TextView content;
    }
}
