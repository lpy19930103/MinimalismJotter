package com.lipy.jotter.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import com.lipy.jotter.R
import com.lipy.jotter.constants.Constant
import com.lipy.jotter.constants.StorageConfig
import com.lipy.jotter.dao.daocore.Note
import com.lipy.jotter.utils.Logger
import com.lipy.jotter.utils.PermissionUtils
import java.io.File
import java.util.*

/**
 * Created by lipy on 2017/3/27.
 */
class HandleMsgActivity : Activity() {
    private var imageFile: File? = null
    private val mNote = Note()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_msg)

        if (PermissionUtils.isAllowUseCamera(this.packageManager)) {
            val lunchName = intent.getStringExtra(Constant.LUNCH_NAME)
            if (lunchName != null && lunchName == Constant.LUNCH_CAMERA) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                imageFile = StorageConfig.creatPicFile(this)
                val imageUri = Uri.fromFile(imageFile)
                Logger.i("imageUri: " + imageUri)
                // set the image file name
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, Constant.HANDLE_MSG_TO_LAUNCH_CAMERA)
            }
        } else {
            val mPermissions = ArrayList<String>()
            mPermissions.add(Manifest.permission.CAMERA)
            mPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            mPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            PermissionUtils.requestMultiPermissions(this, mPermissions) { }
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Logger.i("HandleMsgActivity---resultCode = $resultCode requestCode = $requestCode")
        if (resultCode == 0) {
            finish()
        } else {
            when (requestCode) {
                Constant.HANDLE_MSG_TO_LAUNCH_CAMERA -> {
                    mNote.imagePath = imageFile!!.toString()
                    Logger.i("HandleMsgActivity---mNote.getImagePath: " + mNote.imagePath)
                    NoteEditActivity.startup(this@HandleMsgActivity, mNote, Constant.CREATE_NOTE_MODE)
                    finish()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }
}
