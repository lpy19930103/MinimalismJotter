package com.lipy.jotter.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.lipy.jotter.R
import com.lipy.jotter.dao.NoteService
import com.lipy.jotter.dao.TagService
import com.lipy.jotter.dao.daocore.Note
import com.lipy.jotter.dao.daocore.Tag
import com.lipy.jotter.ui.adapter.ToggleMenuAdapter
import com.lipy.jotter.ui.fragment.NoteListFragment
import com.lipy.jotter.utils.Logger
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 首页
 * Created by lipy on 2017/3/6.
 */
class NoteListActivity : BaseActivity(), View.OnClickListener{

    private var mToolbar: Toolbar? = null
    private var mNoteListFragment: NoteListFragment? = null
    private var adapter: ToggleMenuAdapter? = null
    private var mPopupwindow: PopupWindow? = null
    private var isVertical = true
    private var lableList: ListView? = null
    private var tvallthing: TextView? = null
    private var tagList: ArrayList<Tag> = ArrayList()
    private var mDrawerLayout : DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        initToolBar()
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout?
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
        tvallthing = findViewById(R.id.tv_allthingnum) as TextView?
        val rlshowLable = findViewById(R.id.rl_label) as RelativeLayout?
        val rlsetting = findViewById(R.id.rl_setting) as RelativeLayout?
        val allthing = findViewById(R.id.rl_allthing) as RelativeLayout?
        val rlguide = findViewById(R.id.rl_guide) as RelativeLayout?
        rlshowLable!!.setOnClickListener(this)
        rlsetting!!.setOnClickListener(this)
        allthing!!.setOnClickListener(this)
        rlguide!!.setOnClickListener(this)
        getNoteTag()
        if (tvallthing != null) {
            tvallthing!!.text = NoteService.loadAll().size.toString()
        }
        adapter = ToggleMenuAdapter(this@NoteListActivity, tagList)
        lableList!!.adapter = adapter
        lableList!!.setOnItemClickListener({
            _, _, position, _ ->
            Logger.d(tagList[position].tag)
            mNoteListFragment!!.tag = tagList[position].tag
            mToolbar!!.title = tagList[position].tag
            mDrawerLayout!!.closeDrawers()
        })
    }


    private fun initToolBar() {
        mToolbar = findViewById(R.id.toolbar) as Toolbar?
        mToolbar!!.title = "全部笔记"
        setSupportActionBar(mToolbar)
    }

    override fun onResume() {
        super.onResume()
        if (tvallthing != null) {
            tvallthing!!.text = NoteService.loadAll().size.toString()
        }
        getNoteTag()
        adapter!!.notifyDataSetChanged()
    }

    fun getNoteTag() {
        Observable.create(ObservableOnSubscribe<List<Note>> { e -> e.onNext(NoteService.loadAll()) }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { notes ->
                    val tags = TagService.loadAll()
                    for (tag in tags) {
                        var i = 0
                        for (note in notes) {
                            if (tag.tag == "全部笔记") {
                                i = notes.size
                            } else if (tag.tag == note.tag) {
                                i++
                            }
                        }
                        tag.size = i
                        TagService.saveTag(tag)
                    }
                    Observable.fromArray(tags)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { tags ->
                    tagList.clear()
                    tagList.addAll(tags)
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