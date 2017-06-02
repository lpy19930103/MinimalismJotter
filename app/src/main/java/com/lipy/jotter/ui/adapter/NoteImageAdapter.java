package com.lipy.jotter.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lipy.jotter.R;
import com.lipy.jotter.ui.listener.OnImageClickListener;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.StringUtils;

import java.io.File;
import java.util.List;

/**
 * 写笔记页面照片列表
 * Created by lipy on 2017/5/27.
 */

public class NoteImageAdapter extends BaseAdapter<String> {

    private OnImageClickListener mOnImageClickListener;

    private Context mContext;

    private List<String> url;

    public NoteImageAdapter(Context context, List<String> urls) {
        super(R.layout.image_view_list_adapter, urls);
        url = urls;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnImageClickListener onImageClickListener) {
        this.mOnImageClickListener = onImageClickListener;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (mOnImageClickListener != null) {
            mOnImageClickListener.onImageClick(position);
        }
    }

    @Override
    public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
        showMoreMenu(view, position);
        return true;
    }

    @Override
    protected void convert(BaseViewHolder holder, String item) {
        ImageView image = holder.getView(R.id.iv_image);
        holder.addOnClickListener(R.id.iv_image);
        holder.addOnLongClickListener(R.id.iv_image);
        setImage(item, image);
    }

    private void setImage(String path, ImageView image) {
        Logger.INSTANCE.i("NoteEditActivity----(new File(path): " + new File(path));
        if (StringUtils.isNotEmpty(path)) {
            Glide.with(mContext)
                    .load(new File(path))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.image_loading))
                    .error(mContext.getResources().getDrawable(R.drawable.image_loading))
                    .centerCrop()
                    .crossFade()
                    .skipMemoryCache(true)//跳过内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)//保存最终图片
                    .into(image);
        }
    }

    private void showMoreMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.getMenuInflater()
                .inflate(R.menu.photo_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageDelete(url.get(position));
                }
                return true;
            }
        });
        popup.show();
    }
}
