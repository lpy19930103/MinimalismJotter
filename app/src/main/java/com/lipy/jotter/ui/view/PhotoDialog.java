package com.lipy.jotter.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lipy.jotter.R;
import com.lipy.jotter.utils.StringUtils;

import java.io.File;

/**
 * Created by lipy on 2017/6/1.
 */

public class PhotoDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private ImageView photoImage;

    public PhotoDialog(Context context) {
        super(context, R.style.draw_dialog);
        mContext = context;
        init();
    }

    public void setImagePath(String imagePath) {
        if (StringUtils.isNotEmpty(imagePath) && photoImage != null) {
            Glide.with(mContext)
                    .load(new File(imagePath))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.image_loading))
                    .error(mContext.getResources().getDrawable(R.drawable.image_loading))
                    .crossFade()
                    .fitCenter()
                    .skipMemoryCache(true).//跳过内存缓存
                    diskCacheStrategy(DiskCacheStrategy.RESULT)//保存最终图片
                    .into(photoImage);
        }

    }

    private void init() {
        View view = View.inflate(mContext, R.layout.dialog_photo_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoImage = (ImageView) view.findViewById(R.id.photo_image);
        view.findViewById(R.id.photo_close).setOnClickListener(this);
        view.findViewById(R.id.photo_back).setOnClickListener(this);
        setContentView(view, layoutParams);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_close:
            case R.id.photo_back:
                dismiss();
                break;
        }
    }
}
