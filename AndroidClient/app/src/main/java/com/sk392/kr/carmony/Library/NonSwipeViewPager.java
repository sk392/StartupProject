package com.sk392.kr.carmony.Library;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.google.firebase.crash.FirebaseCrash;

import java.lang.reflect.Field;

public class NonSwipeViewPager extends ViewPager {
    private static final String TAG = "NonSwipeViewPager";
    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public NonSwipeViewPager(Context context,AttributeSet attr) {
        super(context,attr);
        setMyScroller();
    }
    public NonSwipeViewPager(Context context) {

        super(context);
        setMyScroller();
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    //down one is added for smooth scrolling

    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "아몰랑 Fail");
            FirebaseCrash.report(e);
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        //스크롤링되는 제어하는 부분
        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            //duration은 값에 반비례해 스크롤링되는 속도에 반영된다.
            super.startScroll(startX, startY, dx, dy, 120 /*1 secs*/);
        }
    }
}