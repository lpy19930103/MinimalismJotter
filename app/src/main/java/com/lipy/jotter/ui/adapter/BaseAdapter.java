package com.lipy.jotter.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by lipy on 2017/6/2.
 */

public abstract class BaseAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemChildLongClickListener{

    public BaseAdapter(int id, List<T> list) {
        super(id, list);
        this.setOnItemChildClickListener(this);
        this.setOnItemChildLongClickListener(this);
    }


    @Override
    protected abstract void convert(BaseViewHolder holder, T item);

}
