package com.lipy.jotter.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.lipy.jotter.R


/**
 * 引导用户登录注册页
 * Created by lipy on 2017/5/17
 */
class GuideActivity : BaseActivity(), View.OnClickListener{


    private var btnSync: Button? = null
    private var btnLogin: Button? = null
    private var btnRegist: Button? = null
    private var userName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_layout)
        init()
    }

    private fun init() {
        btnLogin = findViewById(R.id.login) as Button?
        btnSync = findViewById(R.id.sync) as Button?
        btnRegist = findViewById(R.id.regist) as Button?
        btnLogin!!.setOnClickListener(this)
        btnSync!!.setOnClickListener(this)
        btnRegist!!.setOnClickListener(this)
        userName = findViewById(R.id.user_name) as TextView?
    }

    override fun onResume() {
        super.onResume()
        if (false) {
        } else {
            btnSync!!.visibility = View.GONE
            btnLogin!!.visibility = View.VISIBLE
            btnRegist!!.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sync -> Toast.makeText(this, "同步", Toast.LENGTH_SHORT).show()
        }
    }

}
