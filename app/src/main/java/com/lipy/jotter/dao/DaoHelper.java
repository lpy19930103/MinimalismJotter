package com.lipy.jotter.dao;

import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.dao.daocore.Tag;
import com.lipy.jotter.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by lipy on 2017/5/27.
 */

public class DaoHelper {
    public static void loadAll() {

    }

    public static void getNoteTag() {
        Observable.create(new ObservableOnSubscribe<List<Note>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Note>> e) throws Exception {
                e.onNext(NoteService.loadAll());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<Note>, ObservableSource<List<Tag>>>() {
                    @Override
                    public ObservableSource<List<Tag>> apply(List<Note> notes) throws Exception {
                        List<Tag> tags = TagService.loadAll();
                        for (Tag tag : tags) {
                            int i = 0;
                            for (Note note : notes) {
                                if (tag.getTag().equals(note.getTag())) {
                                    i++;
                                }
                            }
                            tag.setSize(i);
                            TagService.saveTag(tag);
                        }
                        return Observable.fromArray(tags);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Tag>>() {
                    @Override
                    public void accept(List<Tag> tags) throws Exception {
                        for (Tag tag : tags) {
                            Logger.INSTANCE.e("tag = " + tag.getTag() + ",size = " + tag.getSize());
                        }
                    }
                });
    }

    public void filtrateNote(final String tag) {
        Observable.create(new ObservableOnSubscribe<List<Note>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Note>> e) throws Exception {
                ArrayList<Note> tagNotes = new ArrayList<>();
                for (Note note : NoteService.loadAll()) {
                    if (tag.equals(note.getTag())) {
                        tagNotes.add(note);
                    }
                }
                e.onNext(tagNotes);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> notes) throws Exception {
                        for (Note tag : notes) {
                            Logger.INSTANCE.e("tag = " + tag.getTag());
                        }
                    }
                });
    }
}
