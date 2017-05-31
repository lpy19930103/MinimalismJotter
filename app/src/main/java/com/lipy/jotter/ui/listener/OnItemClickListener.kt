package com.lipy.jotter.ui.listener


/**
 * 笔记列表的点击事件
 * Created by lipy on 2017/4/7.
 */
interface OnItemClickListener {

    fun onItemClick(position: Int)

    fun onEditClick(position: Int)

    fun onDeleteClick(position: Int)

}
