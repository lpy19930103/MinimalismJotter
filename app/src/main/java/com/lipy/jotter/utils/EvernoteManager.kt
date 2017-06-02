package com.lipy.jotter.utils

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.asyncclient.EvernoteCallback
import com.evernote.edam.type.Notebook
import com.evernote.edam.type.User
import com.lipy.jotter.app.App
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by lipy on 2017/5/31.
 */

class EvernoteManager {

    private var mEvernoteListener: EvernoteListener? = null

    companion object {
        private var mEvernoteManager: EvernoteManager? = null

        fun getInstance(): EvernoteManager? {
            if (mEvernoteManager == null) {
                mEvernoteManager = EvernoteManager()
            }
            return mEvernoteManager
        }
    }

    /**
     * 获取印象笔记用户信息
     */
    fun getUser() {
        Observable.create(ObservableOnSubscribe<User> { e ->
            val userStoreClient = EvernoteSession.getInstance().evernoteClientFactory.userStoreClient
            e.onNext(userStoreClient.user)
            e.onComplete()
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<com.evernote.edam.type.User> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(value: User) {
                if (mEvernoteListener != null) {
                    Logger.e("value = " + value.toString() + " == " + mEvernoteListener.toString())
                    mEvernoteListener!!.getUser(value)
                }
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {

            }
        })
    }


    interface EvernoteListener {
        fun getUser(user: User)
    }

    fun setEvernoteListener(evernoteListener: EvernoteListener): EvernoteManager? {
        mEvernoteListener = evernoteListener
        return mEvernoteManager
    }


    fun pullEvernote() {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return
        }
        Observable.create(ObservableOnSubscribe<List<Notebook>> { e ->





            val noteStoreClient = EvernoteSession.getInstance().evernoteClientFactory.noteStoreClient
            noteStoreClient.listNotebooksAsync(object : EvernoteCallback<List<Notebook>> {
                override fun onSuccess(result: List<Notebook>) {
                    val namesList = ArrayList<String>(result.size)
                    for (notebook in result) {
                        notebook.toString()
                        namesList.add(notebook.name)
                        Log.e("EvernoteManager", " notebook"+ notebook.toString())

                    }
                    val notebookNames = TextUtils.join(",", namesList)
                    Toast.makeText(App.instances!!.applicationContext, notebookNames + " notebooks have been retrieved", Toast.LENGTH_LONG).show()
                    e.onNext(result)
                    e.onComplete()
                }

                override fun onException(exception: Exception) {
                    Log.e("EvernoteManager", "Error retrieving notebooks", exception)
                }
            })

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Notebook>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(value: List<Notebook>) {

                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {

                    }
                })

    }

}
