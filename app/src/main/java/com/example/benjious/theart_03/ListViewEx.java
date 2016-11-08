package com.example.benjious.theart_03;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import ui.HorizontalScrollViewEx2;

import static android.R.attr.x;
import static android.os.Build.VERSION_CODES.M;

/**
 * 内部拦截法,是指事件直接到了子View,由子View来决定分发
 * Created by benjious on 2016/11/7.
 */

public class ListViewEx extends ListView {
    public static final String TAG = "LIisViewEx";
    public  static int mLastX = 0;
    public  static int mLastY = 0;


    private HorizontalScrollViewEx2 mHorizontalScrollViewEx2;

    public ListViewEx(Context context) {
        super(context);
    }

    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHorizontalScrollViewEx2(HorizontalScrollViewEx2 horizontalScrollViewEx2) {
        mHorizontalScrollViewEx2 = horizontalScrollViewEx2;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //相对于自身的位置
         int x = (int) ev.getX();
         int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mHorizontalScrollViewEx2.requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int delaY = y - mLastY;
                //这里肯定是父类来处理的啊
                if (Math.abs(deltaX)>Math.abs(delaY)) {
                    mHorizontalScrollViewEx2.requestDisallowInterceptTouchEvent(false);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }

            default:
                break;
        }
        mLastX=x;
        mLastY=y;

        return super.dispatchTouchEvent(ev);
    }


}