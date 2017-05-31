package com.lipy.jotter.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lipy.jotter.R;


/**
 * 引导用户登录注册页
 * Created by lipy on 2017/5/17
 */
public class GuideActivity extends Activity implements LinearLayout.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_layout);
        init();
    }

    private void init() {
        Button btn_login = (Button) findViewById(R.id.login);
        Button btn_regist = (Button) findViewById(R.id.regist);
        Button btn_sync = (Button) findViewById(R.id.sync);
        btn_login.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
        btn_sync.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                Toast.makeText(this, "登录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.regist:
                Toast.makeText(this, "注册", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sync:
                Toast.makeText(this, "同步", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
