package com.lipy.jotter.dao;

import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.utils.Logger;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lipy on 2017/5/27.
 */

public class DaoHelper {
    public static void loadAll() {
        Observable.create(new ObservableOnSubscribe<List<Note>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Note>> e) throws Exception {
                e.onNext(NoteService.loadAll());
                e.onComplete();
            }


        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Note>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Logger.INSTANCE.e("onSubscribe");
            }

            @Override
            public void onNext(List<Note> value) {
                Logger.INSTANCE.e(value.toString());
            }

            @Override
            public void onError(Throwable e) {
                Logger.INSTANCE.e(e.toString());
            }

            @Override
            public void onComplete() {
                Logger.INSTANCE.e("onComplete");
            }
        });
    }
}
