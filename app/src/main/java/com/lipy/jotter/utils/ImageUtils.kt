package com.lipy.jotter.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lipy.jotter.R
import java.io.File

/**
 * Created by lipy on 2017/5/16.
 */

object ImageUtils {

    fun showThumbnail(context: Context, noteImagePath: String, imageView: ImageView) {
        if (StringUtils.isNotEmpty(noteImagePath)) {
            val strings = noteImagePath.replace("[\\[\\]\\t\\r\\n\\s*]".toRegex(), "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val path = strings[strings.size - 1]
            if (StringUtils.isNotEmpty(path)) {
                Glide.with(context)
                        .load(File(path))
                        .fitCenter()
                        .centerCrop()
                        .error(context.resources.getDrawable(R.drawable.image_loading))
                        .into(imageView)
            }
        } else {

        }
    }

    fun adjustImage(wm: WindowManager, absolutePath: String): Bitmap {
        var bm: Bitmap
        val opt = BitmapFactory.Options()
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true
        bm = BitmapFactory.decodeFile(absolutePath, opt)

        // 获取到这个图片的原始宽度和高度
        val picWidth = opt.outWidth
        val picHeight = opt.outHeight

        // 获取屏的宽度和高度
        val display = wm.defaultDisplay
        val screenWidth = display.width
        val screenHeight = display.height

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth) {
                opt.inSampleSize = picWidth / screenWidth
            }
        } else {
            if (picHeight > screenHeight) {
                opt.inSampleSize = picHeight / screenHeight
            }
        }

        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false
        bm = BitmapFactory.decodeFile(absolutePath, opt)

        return bm
    }
}
