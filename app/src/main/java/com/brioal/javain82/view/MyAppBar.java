package com.brioal.javain82.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Brioal on 2016/7/12.
 */

public class MyAppBar extends CoordinatorLayout {
    public MyAppBar(Context context) {
        super(context);
    }

    public MyAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
