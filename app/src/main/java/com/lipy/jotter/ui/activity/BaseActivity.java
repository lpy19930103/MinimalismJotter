package com.lipy.jotter.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.evernote.client.android.EvernoteSession;
import com.lipy.jotter.constants.Constant;

/**
 * Created by lipy on 2017/5/31.
 */

public class BaseActivity extends AppCompatActivity {

    protected EvernoteSession mEvernoteSession;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(Constant.EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(Constant.CONSUMER_KEY, Constant.CONSUMER_SECRET)
                .asSingleton();
    }
}
