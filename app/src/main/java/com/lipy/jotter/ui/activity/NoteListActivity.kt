package com.lipy.jotter.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.evernote.client.android.EvernoteSession
import com.evernote.edam.type.User
import com.lipy.jotter.R
import com.lipy.jotter.dao.NoteService
import com.lipy.jotter.dao.daocore.Note
import com.lipy.jotter.ui.adapter.ToggleMenuAdapter
import com.lipy.jotter.ui.fragment.NoteListFragment
import com.lipy.jotter.utils.EvernoteManager
import java.util.*

/**
 * 首页
 * Created by lipy on 2017/3/6.
 */
class NoteListActivity : BaseActivity(), View.OnClickListener, EvernoteManager.EvernoteListener {

    override fun getUser(user: User) {
        (findViewById(R.id.tv_cancle) as TextView).text = "Evernote：" + user.username
    }


    private var mToolbar: Toolbar? = null
    private var mNoteListFragment: NoteListFragment? = null
    private val menu = ArrayList<Note>()
    private var adapter: ToggleMenuAdapter? = null
    private var mPopupwindow: PopupWindow? = null
    private var isVertical = true
    private var lableList: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        initToolBar()
        val mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout?
        mNoteListFragment = NoteListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_note_list, mNoteListFragment)
        fragmentTransaction.commit()
        initSlid()
        val mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }
        }
        mDrawerToggle.syncState()
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)//设置监听器
    }


    private fun initSlid() {
        lableList = findViewById(R.id.left_drawer) as ListView?
        val showLable = findViewById(R.id.iv_label) as ImageView?
        val tvallthing = findViewById(R.id.tv_allthingnum) as TextView?
        val rlshowLable = findViewById(R.id.rl_label) as RelativeLayout?
        val rlsetting = findViewById(R.id.rl_setting) as RelativeLayout?
        val allthing = findViewById(R.id.rl_allthing) as RelativeLayout?
        val rlguide = findViewById(R.id.rl_guide) as RelativeLayout?
        rlshowLable!!.setOnClickListener(this)
        rlsetting!!.setOnClickListener(this)
        allthing!!.setOnClickListener(this)
        rlguide!!.setOnClickListener(this)
        menu.clear()
        val list = NoteService.loadAll()
        if (tvallthing != null) {
            tvallthing.text = list.size.toString() + ""
        }
        for (i in list.indices.reversed()) {
            menu.add(list[i])
        }
        if (menu.size > 0) {
            adapter = ToggleMenuAdapter(this@NoteListActivity, menu)
            lableList!!.adapter = adapter
        }

    }

    private fun initToolBar() {
        mToolbar = findViewById(R.id.toolbar) as Toolbar?
        mToolbar!!.title = "全部笔记"
        setSupportActionBar(mToolbar)
    }

    override fun onResume() {
        super.onResume()
        if (EvernoteSession.getInstance().isLoggedIn) {
            EvernoteManager.getInstance()!!.setEvernoteListener(this)!!.getUser()
        }
    }

    /**
     * *显示溢出菜单图标
     */
    override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.javaClass.getSimpleName() == "MenuBuilder") {
                try {
                    val m = menu.javaClass.getDeclaredMethod(
                            "setOptionalIconsVisible", java.lang.Boolean.TYPE)
                    m.setAccessible(true)
                    m.invoke(menu, true)
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }

            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.base_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
        //            case R.id.action_edit:
        //                if (mNoteListFragment != null) {
        //                    mNoteListFragment.newNote();
        //                }
        //                break;
            R.id.action_settings -> showSettingPoPu()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showSettingPoPu() {
        if (mPopupwindow != null && mPopupwindow!!.isShowing) {
            mPopupwindow!!.dismiss()
            return
        }
        val customView = layoutInflater.inflate(R.layout.item_note_list_popup, null, false)
        val listOrCard = customView.findViewById(R.id.tv_note_list) as TextView
        if (isVertical) {
            listOrCard.text = "卡片"
        } else {
            listOrCard.text = "列表"
        }
        // 创建PopupWindow实例,设置宽度和高度
        mPopupwindow = PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mPopupwindow!!.animationStyle = R.style.AnimationFade
        mPopupwindow!!.showAsDropDown(customView)
        customView.setOnTouchListener { v, event ->
            if (mPopupwindow != null && mPopupwindow!!.isShowing) {
                mPopupwindow!!.dismiss()
                mPopupwindow = null
            }
            false
        }
        listOrCard.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_note_list -> {
                if (mPopupwindow != null && mPopupwindow!!.isShowing) {
                    mPopupwindow!!.dismiss()
                }
                if (mNoteListFragment != null) {
                    if (isVertical) {
                        mNoteListFragment!!.setCardNote()
                    } else {
                        mNoteListFragment!!.setListNote()
                    }
                }
                isVertical = !isVertical
            }
            R.id.rl_label -> if (lableList!!.visibility == View.VISIBLE) {
                lableList!!.visibility = View.GONE
            } else {
                lableList!!.visibility = View.VISIBLE
            }
            R.id.rl_setting -> gotoSetting()
            R.id.rl_allthing -> {
            }
            R.id.rl_guide -> gotoGuide()
        }

    }


    private fun gotoGuide() {
        val intent = Intent(this, GuideActivity::class.java)
        startActivity(intent)
    }

    private fun gotoSetting() {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }
}