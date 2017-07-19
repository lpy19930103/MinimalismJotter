package com.lipy.jotter.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hitomi.glideloader.GlideImageLoader;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressPieIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.lipy.jotter.R;
import com.lipy.jotter.constants.Constant;
import com.lipy.jotter.dao.NoteService;
import com.lipy.jotter.dao.TagService;
import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.dao.daocore.Tag;
import com.lipy.jotter.ui.adapter.NoteImageAdapter;
import com.lipy.jotter.ui.appwidget.AppWidgetNotesProvider;
import com.lipy.jotter.ui.fragment.TagFragment;
import com.lipy.jotter.ui.listener.OnImageClickListener;
import com.lipy.jotter.ui.view.PhotoDialog;
import com.lipy.jotter.ui.view.pic.AlbumActivity;
import com.lipy.jotter.ui.view.pic.popwindow.SelectPicPopupWindow;
import com.lipy.jotter.utils.FileUtils;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.PermissionUtils;
import com.lipy.jotter.utils.RecoderManager;
import com.lipy.jotter.utils.StringUtils;
import com.lipy.jotter.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lipy.jotter.constants.Constant.EDIT_TO_LAUNCH_CAMERA;
import static com.lipy.jotter.constants.StorageConfig.creatPicFile;
import static com.lipy.jotter.constants.StorageConfig.creatVoiceFile;


/**
 * 写笔记
 * Created by lipy on 2017/3/6.
 */
public class NoteEditActivity extends BaseActivity implements View.OnClickListener, OnImageClickListener {

    private static final int TAG_REQUEST_CODE = 2001;

    private EditText mEtContent;
    private RelativeLayout titleItem;
    private LinearLayout recoderItem;
    private Chronometer recodTime; // 计时组件
    private TextView mTvTime;
    private RecyclerView imageListView;
    private int noteMode;
    private Note mNote;
    private String imagePath;
    private String[] imagePathList;
    private String voicePath;
    private RelativeLayout voiceInfoItem;
    private TextView voiceName;
    private TextView voiceSize;
    private View mAddNoteView;
    private View mCameraView;
    private View mMenuMoreView;
    private RelativeLayout voicePlayerItem;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private SeekBar voiceSeekBar;
    private Chronometer playTime;
    private PopupWindow FunctionPopup;
    private File imageFile;
    private ArrayList listImage = new ArrayList<>();
    private AlertDialog mDialog;
    private int picIndex;
    private String tag;

    View.OnClickListener OnMenuMoreCheck = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initmPopupWindowFunctionListView();
        }
    };

    View.OnClickListener playVoice = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            voiceInfoItem.setVisibility(View.GONE);
            voicePlayerItem.setVisibility(View.VISIBLE);
            voicePlayerItem.setOnClickListener(playOrPause);

            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            } else {
                mPlayer.stop();
            }
            if (mPlayer != null && mPlayer.isPlaying()) {//正在播放音频
                RecoderManager.voicePause(mPlayer, playTime);
            } else {
                RecoderManager.voicePlay(mPlayer, voicePath, voiceSeekBar, playTime);
            }
        }
    };

    View.OnClickListener playOrPause = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPlayer != null && mPlayer.isPlaying()) {//正在播放音频
                RecoderManager.voicePause(mPlayer, playTime);
            } else {
                RecoderManager.voicePlay(mPlayer, voicePath, voiceSeekBar, playTime);
            }
        }
    };

    View.OnClickListener stopRecord = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            recodTime.stop();
            RecoderManager.stopRecord(mRecorder);
            saveNote();
            refreshView();
        }
    };


    View.OnClickListener save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (imageListView.getVisibility() == View.VISIBLE) {//展示图片列表的编辑界面时，返回上一界面
                saveNote();
            } else {//展示大图返回图片列表界面
                showImgList(true);
            }
        }
    };
    private NoteImageAdapter mNoteImageAdapter;
    private PhotoDialog mPhotoDialog;
    private Transferee transferee;
    private LinearLayoutManager linearLayoutManager;
    private TransferConfig config;
    private RelativeLayout imageGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        transferee = Transferee.getDefault(this);
        noteMode = getIntent().getIntExtra(Constant.INTENT_NOTE_MODE, Constant.CREATE_NOTE_MODE);
        mNote = (Note) getIntent().getSerializableExtra(Constant.INTENT_NOTE);
        if (mNote == null) {
            mNote = new Note();
            tag = "全部笔记";
        } else {
            tag = mNote.getTag();
        }

        imagePath = mNote.getImagePath();
        Logger.INSTANCE.i("NoteEditActivity----imagePath: " + imagePath);
        voicePath = mNote.getVoicePath();
        if (StringUtils.isNotEmpty(imagePath) && !"[]".equals(imagePath)) {
            imagePathList = imagePath.replaceAll("[\\[\\]\\t\\r\\n\\s*]", "").split(",");
        }
        initView();
        initViewData(mNote);
    }

    private void initViewData(Note note) {
        if (note != null) {
            if (StringUtils.isNotEmpty(note.getContent())) {
                mEtContent.setText(note.getContent());
            }
            if (note.getLastOprTime() != null) {
                mTvTime.setText(getOprTimeLineText(note));
            } else {
                if (note.getCreateTime() != null) {
                    mTvTime.setText(getString(R.string.note_log_text,
                            getString(R.string.create), TimeUtils.getTime(note.getCreateTime().longValue())));
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String time = formatter.format(curDate);
                    mTvTime.setText(getString(R.string.note_log_text,
                            getString(R.string.create), time));
                }
            }
        }
    }

    private void initView() {
        findViewById(R.id.note_back).setOnClickListener(save);
        titleItem = (RelativeLayout) findViewById(R.id.title_item);
        LinearLayout editItem = (LinearLayout) findViewById(R.id.edit_item);
        recoderItem = (LinearLayout) findViewById(R.id.recorder_item);
        Button recordStop = (Button) findViewById(R.id.record_stop);
        recodTime = (Chronometer) findViewById(R.id.record_time);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        linearLayoutManager = new LinearLayoutManager(this);
        imageListView = (RecyclerView) findViewById(R.id.et_image_listview);
        imageListView.setLayoutManager(linearLayoutManager);
        voiceInfoItem = (RelativeLayout) findViewById(R.id.voice_info_item);
        voiceName = (TextView) findViewById(R.id.voice_name);
        voiceSize = (TextView) findViewById(R.id.voice_size);
        voicePlayerItem = (RelativeLayout) findViewById(R.id.voice_player_item);
        voiceSeekBar = (SeekBar) findViewById(R.id.voice_seekBar);
        playTime = (Chronometer) findViewById(R.id.play_time);
        mAddNoteView = findViewById(R.id.ic_note_add_text_tv);
        mMenuMoreView = findViewById(R.id.ic_widget_menu_more_list_tv);
        mMenuMoreView.setOnClickListener(OnMenuMoreCheck);
        mCameraView = findViewById(R.id.ic_widget_camera_tv);
        imageGroup = (RelativeLayout) findViewById(R.id.image_group);
        mCameraView.setOnClickListener(this);
        mPhotoDialog = new PhotoDialog(this);

        if (noteMode == Constant.CREATE_NOTE_FROM_RECODER_MODE) {
            titleItem.setVisibility(View.GONE);
            recoderItem.setVisibility(View.VISIBLE);
            mTvTime.setVisibility(View.GONE);
            recordStop.setOnClickListener(stopRecord);
            recodTime.start();
            voicePath = creatVoiceFile(this).toString();
            RecoderManager.startRecord(mRecorder, voicePath);
        } else {
            titleItem.setVisibility(View.VISIBLE);
            mTvTime.setVisibility(View.VISIBLE);
            recoderItem.setVisibility(View.GONE);
        }

        //                        saveImageByGlide(imageView);

        //图片列表
        getImagePathList();
        mNoteImageAdapter = new NoteImageAdapter(this, listImage);
        imageListView.setAdapter(mNoteImageAdapter);
        mNoteImageAdapter.setOnItemClickListener(this);

        if (StringUtils.isNotEmpty(voicePath) && noteMode != Constant.CREATE_NOTE_FROM_RECODER_MODE) {
            voiceInfoItem.setVisibility(View.VISIBLE);
            voiceName.setText(StringUtils.getFileName(voicePath));
            try {
                voiceSize.setText(this.getApplicationContext().getString(R.string.edit_voice_size)
                        + FileUtils.FormetFileSize(FileUtils.getFileSize(new File(voicePath))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            voiceInfoItem.setVisibility(View.GONE);
        }
        voiceInfoItem.setOnClickListener(playVoice);
    }

    private void getImagePathList() {
        listImage.clear();
        if (imagePathList != null && imagePathList.length > 0) {
            for (String path : imagePathList) {
                Logger.INSTANCE.i("NoteEditActivity----listImage: " + listImage);
                listImage.add(path);
            }
//            mNoteImageAdapter.notifyDataSetChanged();
        }
    }

    private void saveNote() {
        String title;
        String content = mEtContent.getText().toString().trim();
        if (StringUtils.isEmpty(content) && (listImage == null || listImage.size() < 1)) {
            finish();
            return;
        }
        if (StringUtils.isNotEmpty(content)) {
            if (content.contains("\n")) {
                title = content.split("\n")[0];
            } else {
                title = content;
                if (content.length() > 8) {
                    title = content.substring(0, 8) + "...";
                }
            }
        } else {
            title = getString(R.string.no_title);
        }
        List<Tag> tags = TagService.loadAll();
        ArrayList<String> strings = new ArrayList<>();
        for (Tag arg : tags) {
            strings.add(arg.getTag());
        }
        if (!strings.contains(tag)) {
            TagService.insertTag(new Tag(null, this.tag, 0));
        }
        NoteService.saveNote(mNote, content, title, this.tag, noteMode, listImage.toString(), voicePath);
        finish();
    }

    private void showImgList(boolean showImgList) {
        if (showImgList) {//展示图片列表
            dissmissPhotoDialog();
            mAddNoteView.setVisibility(View.VISIBLE);
            mCameraView.setVisibility(View.VISIBLE);
            imageListView.setVisibility(View.VISIBLE);
            mMenuMoreView.setBackgroundResource(R.drawable.ic_widget_menu_more_list);
            mNoteImageAdapter.notifyDataSetChanged();
        } else {//展示大图
            Logger.INSTANCE.e("lipy", "picIndex = " + picIndex);
            config = TransferConfig.build()
                    .setSourceImageList(listImage)
                    .setMissPlaceHolder(R.drawable.image_loading)
                    .setNowThumbnailIndex(picIndex)
                    .setOriginImageList(wrapOriginImageViewList(listImage.size()))
                    .setProgressIndicator(new ProgressPieIndicator())
                    .setImageLoader(GlideImageLoader.with(this))
                    .setOnLongClcikListener(new Transferee.OnTransfereeLongClickListener() {
                        @Override
                        public void onLongClick(ImageView imageView, int pos) {
//                        saveImageByGlide(imageView);
                        }
                    })
                    .create();

            transferee.apply(config).show(new Transferee.OnTransfereeStateChangeListener() {
                @Override
                public void onShow() {
//                    Glide.with(GlideNoThumActivity.this).pauseRequests();
                }

                @Override
                public void onDismiss() {
//                    Glide.with(GlideNoThumActivity.this).resumeRequests();
                }
            });
//            mPhotoDialog.setImagePath((String) listImage.get(picIndex));
//            mPhotoDialog.show();
        }
    }

    private void dissmissPhotoDialog() {
        if (mPhotoDialog != null && mPhotoDialog.isShowing()) {
            mPhotoDialog.dismiss();
        }
    }

    /**
     * 编辑页功能清单
     * 提醒、清单、发送、查找、详情、删除
     */
    private void initmPopupWindowFunctionListView() {
        View customView = getLayoutInflater().inflate(R.layout.item_note_edit_popup, null, false);
        // 创建PopupWindow实例,设置宽度和高度
        FunctionPopup = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FunctionPopup.setAnimationStyle(R.style.AnimationFade);
        FunctionPopup.showAsDropDown(customView);
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (FunctionPopup != null && FunctionPopup.isShowing()) {
                    FunctionPopup.dismiss();
                    FunctionPopup = null;
                }
                return false;
            }
        });

        TextView tv_note_remind = (TextView) customView.findViewById(R.id.tv_note_remind);
        TextView tv_note_list = (TextView) customView.findViewById(R.id.tv_note_list);
        TextView tv_note_send = (TextView) customView.findViewById(R.id.tv_note_send);
        TextView tv_note_select = (TextView) customView.findViewById(R.id.tv_note_select);
        TextView tv_note_detail = (TextView) customView.findViewById(R.id.tv_note_detail);
        TextView tv_note_delete = (TextView) customView.findViewById(R.id.tv_note_delete);
        tv_note_remind.setOnClickListener(this);
        tv_note_list.setOnClickListener(this);
        tv_note_send.setOnClickListener(this);
        tv_note_select.setOnClickListener(this);
        tv_note_detail.setOnClickListener(this);
        tv_note_delete.setOnClickListener(this);
    }

    /**
     * 确认删除弹框
     *
     * @param url
     */
    private void showDeletePicDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditActivity.this);
        builder.setTitle(R.string.delete_accessory);
        builder.setMessage(R.string.need_delete);
        builder.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        if (FileUtils.deleteFile(url)) {
                        int index = listImage.indexOf(url);
                        Logger.INSTANCE.e(url);
                        FileUtils.deleteFile(url);
                        listImage.remove(url);
                        mNoteImageAdapter.notifyItemRemoved(index);
//                        } else {
//                            Toast.makeText(NoteEditActivity.this, R.string.delete_accessory_fail, Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 笔记实时保存 TODO
        /*if (note_mode == Constant.CREATE_NOTE_MODE) {
            saveNote();//这里应该返回一个保存结果
            //保存成功改变当前状态为编辑状态
            note_mode = Constant.EDIT_NOTE_MODE;
        }*/
        // 笔记实时保存，笔记实时更新widget显示
        appWidgetRefresh();
    }

    /**
     * 通知Appwidget更新显示
     */
    private void appWidgetRefresh() {
        Intent intent = new Intent(AppWidgetNotesProvider.Companion.getCOM_WIDGET_NOTES_LIST_REFRESH());
        sendBroadcast(intent);
    }

    private String getOprTimeLineText(Note note) {
        if (note == null || note.getLastOprTime() == 0) {
            return "";
        }
        String create = getString(R.string.create);
        String edit = getString(R.string.last_update);
        StringBuilder sb = new StringBuilder();
        if (note.getLastOprTime() <= note.getCreateTime() || note.getCreateTime() == 0) {
            sb.append(getString(R.string.note_log_text, create, TimeUtils.getTime(note.getLastOprTime())));
            return sb.toString();
        }
        sb.append(getString(R.string.note_log_text, edit, TimeUtils.getTime(note.getLastOprTime())));
        sb.append("\n");
        sb.append(getString(R.string.note_log_text, create, TimeUtils.getTime(note.getCreateTime())));
        return sb.toString();
    }

    private void refreshView() {
        noteMode = 0;

        titleItem.setVisibility(View.VISIBLE);
        recoderItem.setVisibility(View.GONE);
        voiceInfoItem.setVisibility(View.VISIBLE);
        voiceName.setText(StringUtils.getFileName(voicePath));
        try {
            voiceSize.setText(FileUtils.FormetFileSize(FileUtils.getFileSize(new File(voicePath))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        voiceInfoItem.setOnClickListener(playVoice);
    }

    @Override
    public void onClick(View v) {
        //popup的点击事件处理
        if (v.getId() == R.id.tv_note_remind) {
            if (FunctionPopup != null && FunctionPopup.isShowing()) {
                FunctionPopup.dismiss();
                FunctionPopup = null;
            }
            startActivityForResult(new Intent(this, TagFragment.class), TAG_REQUEST_CODE);
        } else if (v.getId() == R.id.tv_note_list) {

        } else if (v.getId() == R.id.tv_note_send) {

        } else if (v.getId() == R.id.tv_note_select) {

        } else if (v.getId() == R.id.tv_note_detail) {

        } else if (v.getId() == R.id.tv_note_delete) {

        } else if (v.getId() == R.id.ic_widget_camera_tv) {
            //拍照
            if (PermissionUtils.isAllowUseCamera(this.getPackageManager())) {
                selectImgs();

            } else {
                List<String> mPermissions = new ArrayList<>();
                mPermissions.add(Manifest.permission.CAMERA);
                mPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                mPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                PermissionUtils.requestMultiPermissions(this, mPermissions, new PermissionUtils.PermissionGrant() {
                    @Override
                    public void onPermissionGranted(String requestPermission) {

                    }
                });
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (imageListView.getVisibility() == View.VISIBLE) {//展示图片列表的编辑界面时，返回上一界面
                saveNote();
            } else {//展示大图返回图片列表界面
                showImgList(true);
            }
        }
        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_TO_LAUNCH_CAMERA:
                if (resultCode == RESULT_OK) {
                    imageListView.setVisibility(View.VISIBLE);
                    listImage.add(0, imageFile.toString());
                    mNoteImageAdapter.notifyDataSetChanged();
                }
                break;

            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    ArrayList<String> paths = (ArrayList) data.getSerializableExtra("IMAGE_PATHS");
                    for (String path : paths) {
                        if (!listImage.contains(path)) {
                            listImage.add(0, path);
                        }
                    }
                    mNoteImageAdapter.notifyDataSetChanged();
                }
                break;
            case TAG_REQUEST_CODE:
                if (data != null) {
                    tag = data.getStringExtra("TAG");
                    Toast.makeText(NoteEditActivity.this, "设置标签：" + tag, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 启动编辑activity
     *
     * @param activity 调用activity实例
     * @param note
     */
    public static void startup(Activity activity, Note note, int mode) {
        Intent intent = new Intent(activity, NoteEditActivity.class);
        intent.putExtra(Constant.INTENT_NOTE, note);
        intent.putExtra(Constant.INTENT_NOTE_MODE, mode);
        activity.startActivity(intent);
    }

    private SelectPicPopupWindow menuWindow;

    private void selectImgs() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new SelectPicPopupWindow(NoteEditActivity.this, itemsOnClick);
        //设置弹窗位置
        menuWindow.showAtLocation(findViewById(R.id.note_edit_group), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:        //点击拍照按钮
                    goCamera();
                    break;
                case R.id.item_popupwindows_Photo:       //点击从相册中选择按钮
                    Intent intent = new Intent(NoteEditActivity.this,
                            AlbumActivity.class);
                    startActivityForResult(intent, TAKE_PICTURE);
                    break;
                default:
                    break;
            }
        }

    };

    private static final int TAKE_PICTURE = 0;

    private void goCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = creatPicFile(this);
        Logger.INSTANCE.i("NoteEditActivity---imageFile: " + imageFile);
        Uri imageUri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, EDIT_TO_LAUNCH_CAMERA);
    }

    @Override
    public void onImageClick(int position) {
        picIndex = position;
        showImgList(false);
    }

    @Override
    public void onImageDelete(@NotNull String url) {
        showDeletePicDialog(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transferee.destroy();
    }

    /**
     * 包装缩略图 ImageView 集合
     * <p>
     * 注意：此方法只是为了收集 Activity 列表中所有可见 ImageView 好传递给 transferee。
     * 如果你添加了一些图片路径，扩展了列表图片个数，让列表超出屏幕，导致一些 ImageViwe 不
     * 可见，那么有可能这个方法会报错。这种情况，可以自己根据实际情况，来设置 transferee 的
     * originImageList 属性值
     *
     * @return
     */
    @NonNull
    private List<ImageView> wrapOriginImageViewList(int size) {
        List<ImageView> originImgList = new ArrayList<>();


        //得到要更新的item的view
        for (int i = 0; i < size; i++) {
//            if (linearLayoutManager.findViewByPosition(i) != null) {
//                ImageView thumImg = (ImageView) ((RelativeLayout) linearLayoutManager.findViewByPosition(i)).getChildAt(0);
//                originImgList.add(thumImg);
//            }
            ImageView imageView = new ImageView(this);
            imageGroup.addView(imageView);
            originImgList.add(imageView);
        }

        return originImgList;
    }
}
