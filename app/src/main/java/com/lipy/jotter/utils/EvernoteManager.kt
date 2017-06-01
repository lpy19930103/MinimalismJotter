package com.lipy.jotter.utils

import com.evernote.client.android.EvernoteSession
import com.evernote.edam.type.User
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by lipy on 2017/5/31.
 */

class EvernoteManager  {

    private var mEvernoteListener: EvernoteListener? = null

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

    companion object {
        private var mEvernoteManager: EvernoteManager? = null

        fun getInstance(): EvernoteManager? {
            if (mEvernoteManager == null) {
                mEvernoteManager = EvernoteManager()
            }
            return mEvernoteManager
        }
    }

    fun setEvernoteListener(evernoteListener: EvernoteListener): EvernoteManager? {
        mEvernoteListener = evernoteListener
        return mEvernoteManager
    }
}
