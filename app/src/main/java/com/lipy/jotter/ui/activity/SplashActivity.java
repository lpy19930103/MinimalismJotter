package com.lipy.jotter.ui.activity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.widget.ImageView;

import com.lipy.jotter.R;
import com.mcxtzhang.pathanimlib.res.StoreHousePath;
import com.mcxtzhang.pathanimlib.utils.PathParserUtils;


public class SplashActivity extends BaseActivity {

    private ImageView splashBg;
    private LoadingPathAnimView pathAnimView;
    private LoadingPathAnimView pathAnimView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashBg = (ImageView) findViewById(R.id.splash_bg);
        pathAnimView = (LoadingPathAnimView) findViewById(R.id.pathAnimView1);
        pathAnimView2 = (LoadingPathAnimView) findViewById(R.id.pathAnimView2);
        pathAnimView2.setSourcePath(PathParserUtils.getPathFromArrayFloatList(StoreHousePath.getPath("Just for recording")));
        initBg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, NoteListActivity.class));
                finish();
            }
        }.start();
    }

    public void start() {
        pathAnimView.setAnimTime(2500).startAnim();//普通可xml预览path动画
        pathAnimView2.setAnimTime(2500).startAnim();//普通可xml预览path动画

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    private void initBg() {
        // 获取壁纸管理器
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable drawable = wallpaperManager.getDrawable();
        // 获取当前壁纸
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        // 将Drawable,转成Bitmap
        Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        splashBg.setImageBitmap(bm);
    }

    public void stop() {
        pathAnimView.stopAnim();
        pathAnimView2.stopAnim();
    }

    public void reset() {
        pathAnimView.clearAnim();
    }
}
