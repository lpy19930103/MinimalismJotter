package com.lipy.jotter.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lipy.jotter.R;
import com.lipy.jotter.ui.listener.OnImageClickListener;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by lipy on 2017/5/27.
 */

public class NoteImageAdapter extends RecyclerView.Adapter {

    private List<String> mUrls;

    private OnImageClickListener mOnImageClickListener;

    private Context mContext;

    public NoteImageAdapter(Context context, List<String> urls) {
        this.mUrls = urls;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnImageClickListener onImageClickListener) {
        this.mOnImageClickListener = onImageClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view_list_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DefaultViewHolder defaultViewHolder = (DefaultViewHolder) holder;
        defaultViewHolder.setImage(mUrls.get(position));
        defaultViewHolder.setOnItemClickListener(mOnImageClickListener);
    }

    @Override
    public int getItemCount() {
        return mUrls == null ? 0 : mUrls.size();
    }

    private class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnImageClickListener mOnImageClickListener;
        ImageView image;

        DefaultViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
            image.setOnClickListener(this);
        }

        void setOnItemClickListener(OnImageClickListener onImageClickListener) {
            this.mOnImageClickListener = onImageClickListener;
        }

        void setImage(String path) {
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

        @Override
        public void onClick(View v) {
            if (R.id.iv_image == v.getId()) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onClick(getAdapterPosition());
                }
            }
        }
    }
}
