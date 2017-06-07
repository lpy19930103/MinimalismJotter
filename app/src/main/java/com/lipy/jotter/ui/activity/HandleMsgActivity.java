package com.lipy.jotter.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;

import com.lipy.jotter.R;
import com.lipy.jotter.constants.Constant;
import com.lipy.jotter.constants.StorageConfig;
import com.lipy.jotter.dao.daocore.Note;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lipy on 2017/3/27.
 */
public class HandleMsgActivity extends Activity {
    private File imageFile;
    private Note mNote = new Note();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_msg);

        if(PermissionUtils.isAllowUseCamera(this.getPackageManager())) {
            String lunchName = getIntent().getStringExtra(Constant.LUNCH_NAME);
            if (lunchName != null && lunchName.equals(Constant.LUNCH_CAMERA)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = StorageConfig.creatPicFile(this);
                Uri imageUri = Uri.fromFile(imageFile);
                Logger.INSTANCE.i("imageUri: " + imageUri);
                // set the image file name
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, Constant.HANDLE_MSG_TO_LAUNCH_CAMERA);
            }
        } else {
            List<String> mPermissions = new ArrayList<String>();
            mPermissions.add(Manifest.permission.CAMERA);
            mPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            mPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            PermissionUtils.requestMultiPermissions(this, mPermissions, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(String requestPermission) {

                }
            });
            finish();
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.INSTANCE.i("HandleMsgActivity---resultCode = " + resultCode + " requestCode = " + requestCode);
        if (resultCode == 0) {
            finish();
        } else {
            switch (requestCode) {
                case Constant.HANDLE_MSG_TO_LAUNCH_CAMERA:
                    mNote.setImagePath(imageFile.toString());
                    Logger.INSTANCE.i("HandleMsgActivity---mNote.getImagePath: " + mNote.getImagePath());
                    NoteEditActivity.startup(HandleMsgActivity.this, mNote, Constant.CREATE_NOTE_MODE);
                    finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
