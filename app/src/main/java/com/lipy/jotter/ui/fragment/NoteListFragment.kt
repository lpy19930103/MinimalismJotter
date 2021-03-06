package com.lipy.jotter.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.lipy.jotter.R
import com.lipy.jotter.constants.Constant
import com.lipy.jotter.dao.NoteService
import com.lipy.jotter.dao.daocore.Note
import com.lipy.jotter.ui.activity.NoteEditActivity
import com.lipy.jotter.ui.adapter.GridNoteAdapter
import com.lipy.jotter.ui.adapter.NoteAdapter
import com.lipy.jotter.ui.appwidget.AppWidgetNotesProvider
import com.lipy.jotter.ui.listener.OnNoteItemClickListener
import com.lipy.jotter.utils.Logger
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 笔记列表页
 * Created by lipy on 2017/3/6 0006.
 */
class NoteListFragment : Fragment() {

    private var mContext: Activity? = null

    private var mNoteAdapter: NoteAdapter? = null

    private val mNotes = ArrayList<Note>()
    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var recyclerView: RecyclerView? = null
    private var mGridNoteAdapter: GridNoteAdapter? = null

    private var tagString = "全部笔记"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onResume() {
        super.onResume()
        notifyData()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater!!.inflate(R.layout.fragment_notelist, container, false)
        initView(inflate)
        return inflate

    }

    private fun initView(inflate: View) {
        recyclerView = inflate.findViewById(R.id.recycler_view) as RecyclerView
        mLinearLayoutManager = LinearLayoutManager(mContext)
        gridLayoutManager = GridLayoutManager(activity, 2)
        recyclerView!!.layoutManager = mLinearLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        //        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, 1, getResources().getColor(R.color.transparent)));// 添加分割线。
        mNoteAdapter = NoteAdapter(mContext, mNotes)
        mGridNoteAdapter = GridNoteAdapter(mContext, mNotes)
        mNoteAdapter!!.setOnItemClickListener(onItemClickListener)
        mGridNoteAdapter!!.setOnItemClickListener(onItemClickListener)
        recyclerView!!.adapter = mNoteAdapter


        inflate.findViewById(R.id.fab_).setOnClickListener(fabClick)
    }

    fun setTag(tag: String) {
        tagString = tag
        notifyData()
    }

    fun setListNote() {
        recyclerView!!.layoutManager = mLinearLayoutManager
        recyclerView!!.adapter = mNoteAdapter
        notifyData()
    }

    fun setCardNote() {
        recyclerView!!.layoutManager = gridLayoutManager
        //        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        //        recyclerView.addItemDecoration(decoration);
        recyclerView!!.adapter = mGridNoteAdapter
        notifyData()
    }

    /**
     * 跳转到编辑添加笔记界面
     */
    private val fabClick = View.OnClickListener { newNote() }

    fun newNote() {
        NoteEditActivity.startup(this@NoteListFragment.activity, null, Constant.CREATE_NOTE_MODE)
    }


    private val onItemClickListener = object : OnNoteItemClickListener {
        override fun onItemClick(position: Int) {
            go2Edit(mNotes[position])
        }

        override fun onEditClick(position: Int) {
            go2Edit(mNotes[position])
        }

        override fun onDeleteClick(position: Int) {
            deleteNote(position)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            mContext!!.finish()
        }
        return true
    }

    /**
     * ListView数据刷新
     */
    private fun notifyData() {
        Logger.e("tagString = " + tagString)
        filtrateNote(tagString)

    }

    /**
     * 通知Appwidget更新显示
     */
    private fun appWidgetRefresh() {
        val intent = Intent(AppWidgetNotesProvider.COM_WIDGET_NOTES_LIST_REFRESH)
        activity.sendBroadcast(intent)
    }

    /**
     * 修改
     */
    private fun go2Edit(note: Note) {
        NoteEditActivity.startup(this@NoteListFragment.activity, note, Constant.EDIT_NOTE_MODE)
    }

    /**
     * 删除
     */
    private fun deleteNote(position: Int) {
        val note = mNotes[position]
        mNotes.remove(note)
        NoteService.deleteNote(note)
        mNoteAdapter!!.notifyItemRemoved(position)
    }

    private fun filtrateNote(tag: String) {
        Observable.create(ObservableOnSubscribe<List<Note>> { e ->
            var tagNotes = ArrayList<Note>()
            for (note in NoteService.loadAll()) {
                if (tag == "全部笔记") {
                    tagNotes = NoteService.loadAll() as ArrayList<Note>
                } else if (tag == note.tag) {
                    tagNotes.add(note)
                }
            }
            e.onNext(tagNotes)
        })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    mNotes.clear()
                    for (i in notes.indices.reversed()) {
                        mNotes.add(notes[i])
                    }
                    mNoteAdapter!!.notifyDataSetChanged()
                    mGridNoteAdapter!!.notifyDataSetChanged()
                    appWidgetRefresh()
                }
    }
}
