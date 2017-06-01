package com.lipy.jotter.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.lipy.jotter.R

/**
 * 设置功能主页面
 * Created by lipy on 2017/3/29.
 */
class SettingActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        init()
    }

    private fun init() {
        val setting_account_l = findViewById(R.id.setting_account_l) as LinearLayout
        val setting_synchronous_l = findViewById(R.id.setting_synchronous_l) as LinearLayout
        val setting_pwd_lock_l = findViewById(R.id.setting_pwd_lock_l) as LinearLayout
        val setting_input_output_l = findViewById(R.id.setting_input_output_l) as LinearLayout
        val setting_remind_l = findViewById(R.id.setting_remind_l) as LinearLayout
        val setting_more_l = findViewById(R.id.setting_more_l) as LinearLayout
        val setting_feedback_l = findViewById(R.id.setting_feedback_l) as LinearLayout
        val setting_about_l = findViewById(R.id.setting_about_l) as LinearLayout
        setting_account_l.setOnClickListener(this)
        setting_synchronous_l.setOnClickListener(this)
        setting_pwd_lock_l.setOnClickListener(this)
        setting_input_output_l.setOnClickListener(this)
        setting_remind_l.setOnClickListener(this)
        setting_more_l.setOnClickListener(this)
        setting_feedback_l.setOnClickListener(this)
        setting_about_l.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        if (v.id == R.id.setting_account_l) {
            Toast.makeText(this, "点击了列表", Toast.LENGTH_SHORT).show()
        }
    }

}
