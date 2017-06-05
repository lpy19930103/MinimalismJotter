package com.lipy.jotter.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lipy.jotter.R;
import com.lipy.jotter.dao.TagService;
import com.lipy.jotter.dao.daocore.Tag;
import com.lipy.jotter.ui.activity.BaseActivity;
import com.lipy.jotter.ui.view.tag.KeywordsFlow;
import com.lipy.jotter.utils.Logger;
import com.lipy.jotter.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lipy on 2017/6/5.
 */

public class TagFragment extends BaseActivity implements View.OnClickListener {
    private KeywordsFlow mKeywordsFlow;
    private List<String> tagStrs = new ArrayList<>();
    private EditText addET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_fragment);
        init();
        List<Tag> tags = TagService.loadAll();
        if (tags != null && tags.size() > 0) {
            for (Tag tag : tags) {
                tagStrs.add(tag.getTag());
            }
            refreshTags();
        }
    }


    public void init() {
        findViewById(R.id.tag_back).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.tag_setting).setOnClickListener(this);
        mKeywordsFlow = (KeywordsFlow) findViewById(R.id.key_words_flow);
        addET = (EditText) findViewById(R.id.tag_et);
        mKeywordsFlow.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = ((TextView) v).getText().toString();// 获得点击的标签
                setResult(RESULT_OK, getIntent().putExtra("TAG", keyword));
                Logger.INSTANCE.e(keyword);
                finish();
            }
        });
    }


    private void refreshTags() {
        mKeywordsFlow.setDuration(800);
        for (String tag : tagStrs) {
            mKeywordsFlow.feedKeyword(tag);
            Logger.INSTANCE.e(tag);
        }
        mKeywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tag_back:
                finish();
                break;
            case R.id.tag_setting:
                break;
            case R.id.add:
                String addStr = addET.getText().toString().trim();
                if (StringUtils.isNotEmpty(addStr) && !tagStrs.contains(addStr)) {
                    tagStrs.add(addStr);
                    TagService.insertTag(new Tag(null, addStr));
                    refreshTags();
                }
                break;

        }
    }
}
