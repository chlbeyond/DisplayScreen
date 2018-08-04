package com.chl.displayscreen.commonView.DialogImageView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Administrator on 2018/7/30.
 */

public class PageHorizatalScrollView extends HorizontalScrollView {

    public static final int NOT_SCROLL = 0;
    public static final int SCROLL = 1;

    public int mBaseScrollX;//滑动基线。也就是点击并滑动之前的x值，以此值计算相对滑动距离。
    private int mScreenWidth;//屏幕宽
    private int mScreenHeight;
    private int mPageCount;//页面数量

    private int mScrollX = 200;//滑动多长距离翻页


    public PageHorizatalScrollView(Context context) {
        super(context);

        init(context);
    }

    public PageHorizatalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }



    public PageHorizatalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources()
                .getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    /**
     * 获取页面数量
     * @return
     */
    public int getPageCount() {
        return mPageCount;
    }

    /**
     * 获取相对滑动位置。由右向左滑动，返回正值；由左向右滑动，返回负值。
     * @return
     */
    private int getBaseScrollX() {
        return getScrollX() - mBaseScrollX;
    }

    /**
     * 使相对于基线移动x距离。
     * @param x x为正值时右移；为负值时左移。
     */
    public void baseSmoothScrollTo(int x, int orientation) {
        smoothScrollTo(x + mBaseScrollX, 0);
        if (onScrollToIndexListen != null) {
            int index = (x + mBaseScrollX)/mScreenWidth;
            onScrollToIndexListen.scrollToIndex(index, orientation);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getBaseScrollX();

                //手指在划，但没有滑动
                if (scrollX == 0) {//
                    baseSmoothScrollTo(0, PageHorizatalScrollView.NOT_SCROLL);
                }
                //手左滑，大于一半，移到下一页
                else if (scrollX > mScrollX) {
                    baseSmoothScrollTo(mScreenWidth, PageHorizatalScrollView.SCROLL);
                    mBaseScrollX += mScreenWidth;
                    Log.e("TAG", "//手左滑，大于一半，移到下一页");
                }
                //左滑，不到一半，返回原位
                else if (scrollX > 0) {
                    baseSmoothScrollTo(0, PageHorizatalScrollView.SCROLL);
                    Log.e("TAG", " //左滑，不到一半，返回原位");
                }
                //右滑，不到一半，返回原位
                else if(scrollX > -mScrollX) {
                    baseSmoothScrollTo(0, PageHorizatalScrollView.SCROLL);
                    Log.e("TAG", "//右滑，不到一半，返回原位 :" + scrollX);
                }
                //手右滑，大于一半，移到上一页
                else {
                    baseSmoothScrollTo(-mScreenWidth, PageHorizatalScrollView.SCROLL);
                    mBaseScrollX -= mScreenWidth;
                    Log.e("TAG", "//手右滑，大于一半，移到上一页");
                }
                return false;
        }
        return super.onTouchEvent(ev);
    }

    private OnScrollToIndexListen onScrollToIndexListen;

    public void setOnScrollToIndexListen(OnScrollToIndexListen onScrollToIndexListen) {
        this.onScrollToIndexListen = onScrollToIndexListen;
    }

    public interface OnScrollToIndexListen {
        void scrollToIndex(int index, int orientation);
    }
}
