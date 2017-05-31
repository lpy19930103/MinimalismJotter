package com.lipy.jotter.dao;

import com.lipy.jotter.dao.daocore.Note;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lipy on 2017/5/27.
 */

public class DaoHelper {
    public static void loadAll() {
        Observable.create(new ObservableOnSubscribe<List<Note>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Note>> e) throws Exception {

            }


        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Note>>() {
            @Override
            public void accept(List<Note> notes) throws Exception {

            }
        });
    }
}
