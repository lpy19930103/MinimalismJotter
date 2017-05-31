package com.lipy.jotter.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.lipy.jotter.R;
import com.lipy.jotter.utils.ScreenUtil;

import java.lang.reflect.Method;

/**
 * 点击 长按 不弹出菜单项的EditText (不能复制粘贴)
 * Created by lipy on 16/3/10.
 */
public class EditTextExp extends EditText implements
        View.OnFocusChangeListener, TextWatcher,View.OnLongClickListener {
    private float mMLastDownPositionX;
    private float mMLastDownPositionY;
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;

    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    public EditTextExp(Context context) {
        this(context, null);
        init();
    }

    public EditTextExp(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
        init();
    }

    public EditTextExp(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 禁止Edittext弹出软键盘，光标依然正常显示。
     */
    public static void disableShowSoftInput(EditText editText)
    {
        editText.setTextIsSelectable(false);
        if (android.os.Build.VERSION.SDK_INT <= 10)
        {
            editText.setInputType(InputType.TYPE_NULL);
        }
        else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus",boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            }catch (Exception e) {
                // TODO: handle exception
            }

            try {
                method = cls.getMethod("setSoftInputShownOnFocus",boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.drawable.common_edit_delete);
        }

        int a = ScreenUtil.dipToPx(getContext(), 17);
/*        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());*/
        mClearDrawable.setBounds(0, 0, a, a);

        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clearIsClick(event);
        requestFocus();
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mMLastDownPositionX = event.getX();
            mMLastDownPositionY = event.getY();
        }
        int offsetForPosition = getOffsetForPosition(mMLastDownPositionX, mMLastDownPositionY);

        Selection.setSelection(getText(), offsetForPosition < 0 ? 0 : offsetForPosition);
        return true;
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     */
    protected void setClearIconVisible(boolean visible) {
        if(!isEnabled()) {
            visible = false;
        }
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if(!enabled) {
            setClearIconVisible(false);
        }
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
            int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 阻止复制粘贴的功能
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        return true;
    }

    public boolean clearIsClick(MotionEvent event) {
        boolean ret = false;
        if (isEnabled() && event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                    ret = true;
                }
            }
        }
        return ret;
    }

    @Override
    public boolean onLongClick(View view) {
        return true;
    }
}