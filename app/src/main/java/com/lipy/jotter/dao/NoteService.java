package com.lipy.jotter.dao;

import android.text.TextUtils;

import com.lipy.jotter.constants.Constant;
import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.dao.daocore.NoteDao;
import com.lipy.jotter.utils.StringUtils;

import java.util.List;


/**
 * 笔记service，存储加载笔记
 * Created by lipy on 17/4/7.
 */

public class NoteService {

    public static List<Note> loadAll() {
        return DaoService.getInstance().getDaoSession().getNoteDao().loadAll();
    }

    public static void deleteNote(Note note) {
        DaoService.getInstance().getDaoSession().getNoteDao().delete(note);
    }

    /**
     * 保存笔记
     * @param note
     * @param content
     * @param label
     * @param etType 笔记类型 默认为1
     * @param mode
     * @param imgPath
     * @param voicePath
     */
    public static void saveNote(Note note, String content,
                                String label, int etType, int mode, String imgPath, String voicePath) {
        updateNote(note, content, label, etType, mode, imgPath, voicePath);
        saveNote(note, mode);
    }

    private static void updateNote(Note note, String etContent,
                                   String etLabel, int etType, int mode, String imgPath, String voicePath) {
        if (StringUtils.isNotEmpty(etContent)) {
            note.setContent(etContent);
        }
        if (TextUtils.isEmpty(etLabel)) {
            note.setLabel("Not Title");
        } else {
            note.setLabel(etLabel);
        }
        note.setType(etType);
        if (StringUtils.isNotEmpty(imgPath)) {
            note.setImagePath(imgPath);
        }
        switch (mode) {
            case Constant.CREATE_NOTE_MODE:
                note.setCreateTime(System.currentTimeMillis());
                break;
            case Constant.CREATE_NOTE_FROM_RECODER_MODE:
                if (StringUtils.isNotEmpty(voicePath)) {
                    note.setVoicePath(voicePath);
                }
                note.setCreateTime(System.currentTimeMillis());
                break;
            default:
                note.setLastOprTime(System.currentTimeMillis());
                break;
        }
    }

    private static void saveNote(Note note, int mode) {
        NoteDao noteDao = DaoService.getInstance().getDaoSession().getNoteDao();
        switch (mode) {
            case Constant.CREATE_NOTE_MODE:
            case Constant.CREATE_NOTE_FROM_RECODER_MODE:
                long result = noteDao.insert(note);
                break;
            case Constant.EDIT_NOTE_MODE:
                noteDao.update(note);
                break;
            default:
                break;
        }
    }
}
