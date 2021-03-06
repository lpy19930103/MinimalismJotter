package com.lipy.jotter.ui.activity;

import android.app.ActionBar;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lipy.jotter.R;
import com.mcxtzhang.pathanimlib.res.StoreHousePath;
import com.mcxtzhang.pathanimlib.utils.PathParserUtils;


public class SplashActivity extends BaseActivity {

    private ImageView splashBg;
    private LoadingPathAnimView pathAnimView;
    private LoadingPathAnimView pathAnimView2;
    private LoadingPathAnimView pathAnimView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        splashBg = (ImageView) findViewById(R.id.splash_bg);
        pathAnimView = (LoadingPathAnimView) findViewById(R.id.pathAnimView1);
        pathAnimView2 = (LoadingPathAnimView) findViewById(R.id.pathAnimView2);
        pathAnimView2.setSourcePath(PathParserUtils.getPathFromArrayFloatList(StoreHousePath.getPath("Just for recording")));
        pathAnimView3 = (LoadingPathAnimView) findViewById(R.id.pathAnimView3);
        pathAnimView3.setSourcePath(PathParserUtils.getPathFromArrayFloatList(StoreHousePath.getPath("Just for reading")));
        initBg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
        new CountDownTimer(2000, 1000) {
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
        pathAnimView.setAnimTime(2000).startAnim();//普通可xml预览path动画
        pathAnimView2.setAnimTime(2000).startAnim();//普通可xml预览path动画
        pathAnimView3.setAnimTime(2000).startAnim();//普通可xml预览path动画
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
        pathAnimView3.stopAnim();
    }

    public void reset() {
        pathAnimView.clearAnim();
    }
}
