//package com.lipy.jotter.utils;
//
//import android.app.Activity;
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.evernote.client.android.EvernoteSession;
//import com.evernote.client.android.asyncclient.EvernoteCallback;
//import com.evernote.edam.error.EDAMErrorCode;
//import com.evernote.edam.error.EDAMNotFoundException;
//import com.evernote.edam.error.EDAMUserException;
//import com.evernote.edam.notestore.NoteCollectionCounts;
//import com.evernote.edam.notestore.NoteFilter;
//import com.evernote.edam.notestore.NoteMetadata;
//import com.evernote.edam.notestore.NotesMetadataList;
//import com.evernote.edam.notestore.NotesMetadataResultSpec;
//import com.evernote.edam.type.Note;
//import com.evernote.edam.type.NoteSortOrder;
//import com.evernote.edam.type.Notebook;
//import com.evernote.edam.type.User;
//import com.lguipeng.notes.BuildConfig;
//import com.lguipeng.notes.injector.ContextLifeCycle;
//import com.lguipeng.notes.model.SNote;
//
//import net.tsz.afinal.FinalDb;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import de.greenrobot.event.EventBus;
//
///**
// * Created by lipy on 2015/6/12.
// */
//public class EverNoteUtils {
//
//    private EvernoteSession mEvernoteSession;
//
//    private PreferenceUtils mPreferenceUtils;
//
//    private ThreadExecutorPool mThreadExecutorPool;
//
//    private FinalDb mFinalDb;
//
//    public static final String NOTE_BOOK_NAME = "SNotes";
//
//    @Inject
//    public EverNoteUtils( @ContextLifeCycle("App") Context mContext, ThreadExecutorPool pool, FinalDb mFinalDb, PreferenceUtils mPreferenceUtils) {
//        mEvernoteSession = EvernoteSession.getInstance();
//        this.mPreferenceUtils = mPreferenceUtils;
//        this.mThreadExecutorPool = pool;
//        this.mFinalDb = mFinalDb;
//    }
//
//    public boolean isLogin() {
//        return mEvernoteSession != null && mEvernoteSession.isLoggedIn();
//    }
//
//    public void auth(Activity activity){
//        if (activity == null)
//            return;
//        mEvernoteSession.authenticate(activity);
//    }
//
//    public void logout(){
//        mEvernoteSession.logOut();
//        mPreferenceUtils.removeKey(PreferenceUtils.EVERNOTE_ACCOUNT_KEY);
//    }
//
//    public User getUser() throws Exception{
//        return mEvernoteSession.getEvernoteClientFactory()
//                .getUserStoreClient().getUser();
//    }
//
//    public void getUser(EvernoteCallback<User> callback) throws Exception{
//        mEvernoteSession.getEvernoteClientFactory()
//                .getUserStoreClient().getUserAsync(callback);
//    }
//
//    public String getUserAccount(User user) {
//        if (user != null){
//            String accountInfo = user.getEmail();
//            if (!TextUtils.isEmpty(accountInfo)){
//                return accountInfo;
//            }else {
//                accountInfo = user.getUsername();
//            }
//            mPreferenceUtils.saveParam(PreferenceUtils.EVERNOTE_ACCOUNT_KEY, accountInfo);
//            return accountInfo;
//        }
//        return "";
//    }
//
//    private void makeSureNoteBookExist(String notebookName)throws Exception{
//        NotesLog.d("");
//        String guid = mPreferenceUtils
//                .getStringParam(PreferenceUtils.EVERNOTE_NOTEBOOK_GUID_KEY);
//        if (!TextUtils.isEmpty(guid)){
//            Notebook notebook = findNotebook(guid);
//            if (notebook != null && TextUtils.equals(notebook.getName(), notebookName)){
//                mPreferenceUtils.saveParam(PreferenceUtils.EVERNOTE_NOTEBOOK_GUID_KEY,
//                        notebook.getGuid());
//            }else {
//                tryCreateNoteBook(notebookName);
//            }
//        } else {
//            tryCreateNoteBook(notebookName);
//        }
//        NotesLog.d("");
//    }
//
//    private boolean hasNoteBookExist(String guid, String name) throws Exception {
//        boolean result = false;
//        try {
//            Notebook notebook = findNotebook(guid);
//            if (notebook == null)
//                return false;
//            if (notebook.getName().equals(name)) {
//                result = true;
//                mPreferenceUtils.saveParam(PreferenceUtils.EVERNOTE_NOTEBOOK_GUID_KEY
//                        , notebook.getGuid());
//            }
//        } catch (EDAMNotFoundException e) {
//            handleException(e);
//            result = false;
//        }
//        return result;
//    }
//
//    private Notebook findNotebook(String guid) throws Exception {
//        Notebook notebook;
//        try {
//            notebook = mEvernoteSession.getEvernoteClientFactory()
//                    .getNoteStoreClient().getNotebook(guid);
//
//        } catch (EDAMNotFoundException e) {
//            handleException(e);
//            notebook = null;
//        }
//        return notebook;
//    }
//
//    private List<Notebook> listNotebooks() throws Exception{
//        List<Notebook> books = new ArrayList<>();
//        try {
//            books = mEvernoteSession.getEvernoteClientFactory()
//                    .getNoteStoreClient().listNotebooks();
//        } catch (Exception e) {
//            handleException(e);
//        }
//        return books;
//    }
//
//    private Notebook tryCreateNoteBook(String bookName) throws Exception{
//        Notebook notebook = new Notebook();
//        notebook.setName(bookName);
//        try {
//            Notebook result = mEvernoteSession.getEvernoteClientFactory()
//                    .getNoteStoreClient().createNotebook(notebook);
//            mPreferenceUtils.saveParam(PreferenceUtils.EVERNOTE_NOTEBOOK_GUID_KEY
//                    , result.getGuid());
//            return result;
//        }catch (EDAMUserException e){
//            if (e.getErrorCode() == EDAMErrorCode.DATA_CONFLICT) {
//                List<Notebook> books = listNotebooks();
//                for (Notebook book : books){
//                    if (TextUtils.equals(book.getName(), bookName)){
//                        mPreferenceUtils.saveParam(PreferenceUtils.EVERNOTE_NOTEBOOK_GUID_KEY
//                                , book.getGuid());
//                        return book;
//                    }
//                }
//            }
//            handleException(e);
//            return null;
//        }
//    }
//
//    private Note createNote(SNote sNote) throws Exception{
//        if (sNote == null)
//            return null;
//        Note note = sNote.parseToNote();
//        note.setActive(true);
//        String guid = mPreferenceUtils.getStringParam(PreferenceUtils.EVERNOTE_NOTEBOOK_GUID_KEY);
//        note.setNotebookGuid(guid);
//        NotesLog.d(guid);
//        Note result = mEvernoteSession.getEvernoteClientFactory()
//                .getNoteStoreClient().createNote(note);
//        if (result == null)
//            return null;
//        sNote.setGuid(result.getGuid());
//        sNote.setStatus(SNote.Status.IDLE.getValue());
//        //sNote.setCreateTime(result.getCreated());
//        //sNote.setLastOprTime(result.getUpdated());
//        mFinalDb.update(sNote);
//        return result;
//    }
//
//    private Note pushUpdateNote(SNote sNote) throws Exception{
//        Note updateNote = sNote.parseToNote();
//        updateNote.setGuid(sNote.getGuid());
//        updateNote.setActive(true);
//        Note result = mEvernoteSession.getEvernoteClientFactory()
//                .getNoteStoreClient().updateNote(updateNote);
//        sNote.setStatus(SNote.Status.IDLE.getValue());
//        sNote.setLastOprTime(result.getUpdated());
//        mFinalDb.update(sNote);
//        return result;
//    }
//
//    private void pullUpdateNote(SNote sNote) throws Exception{
//        Note note = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient()
//                .getNote(sNote.getGuid(), true, false, false, false);
//        sNote.parseFromNote(note);
//        sNote.setType(SNote.NoteType.NORMAL);
//        mFinalDb.update(sNote);
//    }
//
//    private void loadEverNote(String guid)throws Exception{
//        if (TextUtils.isEmpty(guid))
//            return;
//        Note note = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient()
//                .getNote(guid, true, false, false, false);
//        SNote sNote = new SNote();
//        sNote.parseFromNote(note);
//        mFinalDb.saveBindId(sNote);
//    }
//
//    private void deleteNote(String guid) throws Exception{
//        if (TextUtils.isEmpty(guid))
//            return;
//        mEvernoteSession.getEvernoteClientFactory()
//                .getNoteStoreClient().deleteNote(guid);
//    }
//
//    private void deleteLocalNote(String guid){
//        if (TextUtils.isEmpty(guid))
//            return;
//        try {
//            mFinalDb.deleteByWhere(SNote.class, "guid = '" + guid + "'");
//        }catch (Exception e){
//            NotesLog.e("delete local note error");
//            e.printStackTrace();
//        }
//    }
//
//    public void expungeNote(String guid) throws Exception{
//        if (TextUtils.isEmpty(guid))
//            return;
//        mEvernoteSession.getEvernoteClientFactory()
//                .getNoteStoreClient().expungeNote(guid);
//    }
//
//    public boolean pushNote(SNote sNote)throws Exception{
//        if (sNote == null)
//            return false;
//        if (sNote.hasReadyRemove()){
//            if (!TextUtils.isEmpty(sNote.getGuid())){
//                deleteNote(sNote.getGuid());
//            }
//            sNote.setStatus(SNote.Status.IDLE);
//            mFinalDb.update(sNote);
//        }else if(sNote.hasReadyNewPush()){
//            createNote(sNote);
//        }else if (sNote.hasReadyUpdatePush()){
//            pushUpdateNote(sNote);
//        }
//        return true;
//    }
//
//    public void pushNotes() throws Exception{
//        NotesLog.d("");
//        List<SNote> sNotes = mFinalDb.findAll(SNote.class);
//        for (SNote sNote : sNotes){
//            pushNote(sNote);
//        }
//        NotesLog.d("");
//    }
//
//    public void pullNotes() throws Exception{
//        NotesLog.d("");
//        NoteFilter noteFilter = new NoteFilter();
//        noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());
//        String guid = mPreferenceUtils.getStringParam(PreferenceUtils.EVERNOTE_NOTEBOOK_GUID_KEY);
//
//        noteFilter.setNotebookGuid(guid);
//        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
//        spec.setIncludeUpdated(true);
//        spec.setIncludeCreated(true);
//        NoteCollectionCounts counts = mEvernoteSession.getEvernoteClientFactory()
//                .getNoteStoreClient().findNoteCounts(noteFilter, false);
//        List<SNote> sNoteList = mFinalDb.findAllByWhere(SNote.class,
//                "type != " + SNote.NoteType.TRASH.getValue());
//        List<String> guids = new ArrayList<>();
//        for (SNote note : sNoteList){
//            guids.add(note.getGuid());
//        }
//
//        if (counts == null || counts.getNotebookCounts() == null){
//            for (String deleteGuid :guids){
//                deleteLocalNote(deleteGuid);
//            }
//            return;
//        }
//
//        int maxCount = counts.getNotebookCounts().get(guid);
//
//        NotesMetadataList list = mEvernoteSession.getEvernoteClientFactory()
//                .getNoteStoreClient()
//                .findNotesMetadata(noteFilter, 0, maxCount, spec);
//
//        for (NoteMetadata data : list.getNotes()){
//            guids.remove(data.getGuid());
//            List<SNote> sNotes = mFinalDb.findAllByWhere(SNote.class, "guid = '" + data.getGuid() + "'");
//            if (sNotes != null && sNotes.size() > 0){
//                //update
//                SNote sNote = sNotes.get(0);
//                if (data.getUpdated() > sNote.getLastOprTime())
//                    pullUpdateNote(sNote);
//            }else {
//                //pull
//                loadEverNote(data.getGuid());
//            }
//        }
//        if (guids.size() > 0){
//            for (String deleteGuid :guids){
//                deleteLocalNote(deleteGuid);
//            }
//        }
//        NotesLog.d("");
//    }
//
//    private boolean checkLogin(boolean silence){
//        if (!isLogin()){
//            if (!silence)
//                EventBus.getDefault().post(SyncResult.ERROR_NOT_LOGIN);
//            return false;
//        }
//        return true;
//    }
//
//    public SyncResult checkLogin(){
//        if (!isLogin()){
//            return SyncResult.ERROR_NOT_LOGIN;
//        }
//        return SyncResult.SUCCESS;
//    }
//
//    public SyncResult sync(final SyncType type){
//        if (checkLogin() == SyncResult.ERROR_NOT_LOGIN){
//            return SyncResult.ERROR_NOT_LOGIN;
//        }
//        try {
//            makeSureNoteBookExist(NOTE_BOOK_NAME);
//        }catch (Exception e){
//            e.printStackTrace();
//            if (e instanceof EDAMUserException){
//                EDAMUserException exception = (EDAMUserException)e;
//                EDAMErrorCode errorCode = exception.getErrorCode();
//                switch (errorCode){
//                    case RATE_LIMIT_REACHED:
//                        if (!BuildConfig.DEBUG){
//                            return SyncResult.ERROR_FREQUENT_API;
//                        }
//                        break;
//                    //need to auth again
//                    case AUTH_EXPIRED:
//                        //clear login message
//                        logout();
//                        return SyncResult.ERROR_AUTH_EXPIRED;
//                    case PERMISSION_DENIED:
//                        return SyncResult.ERROR_PERMISSION_DENIED;
//                    //quota reached max, so fail
//                    case QUOTA_REACHED:
//                        return SyncResult.ERROR_QUOTA_EXCEEDED;
//                    default:
//                        return SyncResult.ERROR_OTHER;
//                }
//            }
//            return SyncResult.ERROR_OTHER;
//        }
//        try {
//            switch (type){
//                case ALL:
//                    pushNotes();
//                    pullNotes();
//                    break;
//                case PULL:
//                    pullNotes();
//                    break;
//                case PUSH:
//                    pushNotes();
//                    break;
//            }
//            return SyncResult.SUCCESS;
//        }catch (Exception e){
//            e.printStackTrace();
//            return SyncResult.ERROR_OTHER;
//        }
//    }
//
//    private void handleException(Exception e){
//        if (e != null)
//            e.printStackTrace();
//    }
//
//    public enum SyncResult{
//        START,
//        ERROR_NOT_LOGIN,
//        ERROR_FREQUENT_API,
//        ERROR_EXPUNGE,
//        ERROR_DELETE,
//        ERROR_RECOVER,
//        ERROR_AUTH_EXPIRED,
//        ERROR_PERMISSION_DENIED,
//        ERROR_QUOTA_EXCEEDED,
//        ERROR_OTHER,
//        SUCCESS_SILENCE,
//        SUCCESS
//    }
//
//    public enum SyncType{
//        ALL,
//        PULL,
//        PUSH
//    }
//}
