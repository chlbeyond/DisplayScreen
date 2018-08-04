package com.chl.displayscreen.commonView.DialogImageView;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chl.displayscreen.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/28.
 */

public class DialogImageView extends AppCompatImageView {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_TRANSFORM_IN = 1;
    public static final int STATE_TRANSFORM_OUT = 2;

    public Object imageId;
    private MyRect originalRect;//得到点击的图片相对于window的位置
    private List<MyRect> originalRects;//得到点击的图片相对于window的位置
    public CoverWindow coverWindow;
    public MyGridView gridView;

    public DialogImageView(Context context) {
        super(context);
//        init();
    }

    public DialogImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

    public DialogImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

    private void init() {
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showImageToWindow();
//            }
//        });
    }

    /**
     * 获取每个图片相对于window的位置
     *
     * @return
     */
    private List<MyRect> getOriginalRects() {
        List<MyRect> originalRects = new ArrayList<MyRect>();
        int count = gridView.getChildCount();
        for (int i = 0; i < count; i++) {
//            if (gridView.getChildAt(i) instanceof DialogImageView) {
            View view = gridView.getChildAt(i);
            int[] outLocation = new int[2];
            view.getLocationInWindow(outLocation);
            Log.e("TAG", "outLocation[0]:" + outLocation[0] + "outLocation[1]:" + outLocation[1]);
            MyRect tRect = new MyRect(outLocation[0], outLocation[1], view.getWidth(), view.getHeight());
            originalRects.add(tRect);
//            } else {
//                continue;
//            }

        }
        this.originalRects = originalRects;
        return originalRects;
    }

    public void showImageToWindow(Object path, int position, List<Integer> fileIndexList) {
        int[] outLocation = new int[2];
        getLocationInWindow(outLocation);
//        originalRect = new MyRect(getLeft(),getTop(),getWidth(),getHeight());
//        originalRect = new TRect(outLocation[0],outLocation[1] - TKCUtils.getStatusBarHeight(getContext()),getWidth(),getHeight());
        originalRect = new MyRect(outLocation[0], outLocation[1], getWidth(), getHeight());//保存得到的第一个图片相对于window的位置
//        coverWindow = new CoverWindow(getContext(), originalRect, path, position, fileIndexList, getOriginalRects());
        coverWindow = new CoverWindow(Utils.getActivityFromView(DialogImageView.this), originalRect, path, position, fileIndexList);
        coverWindow.startTransform(DialogImageView.STATE_TRANSFORM_IN);
    }
}
