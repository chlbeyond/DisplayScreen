package com.chl.displayscreen.commonView.DialogImageView;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chl.displayscreen.R;
import com.chl.displayscreen.activity.MainActivity;
import com.chl.displayscreen.commonView.photoview.OnViewTapListener;
import com.chl.displayscreen.commonView.photoview.PhotoView;
import com.chl.displayscreen.utils.LogToFile;

import java.util.List;

/**
 * Created by Administrator on 2018/7/28.
 */

public class CoverWindow extends RelativeLayout {

    public int mState = DialogImageView.STATE_NORMAL;
    private Context context;
    private MainActivity activity;
    private MyRect originalRect;
    //    private List<MyRect> originalRects;
    public Object imageId;
    //    private List<Object> imageIds;
    private int currentIndex;
    private int count;
    private List<Integer> fileIndexList;
    private WindowManager windowManager;
    private ImageView animationIV;
    private PhotoView photoView;
    private ValueAnimator valueAnimator;

    private LinearLayout gridViewBgView;
    private GridView gridView;

    private boolean isCanBack = true;

    public CoverWindow(Context context) {
        super(context);
    }

    public CoverWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoverWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CoverWindow(Context context, MyRect originalRect, Object imageId, int currentIndex, List<Integer> fileIndexList, List<MyRect> originalRects) {
        super(context);
        this.context = context;
        this.activity = (MainActivity) context;
        this.originalRect = originalRect;
        this.imageId = imageId;
        this.currentIndex = currentIndex;
        this.fileIndexList = fileIndexList;
        this.count = fileIndexList.size();
//        this.originalRects = originalRects;
        setBackgroundColor(Color.TRANSPARENT);
        initView();
    }

    public CoverWindow(Context context, MyRect originalRect, Object imageId, int currentIndex, List<Integer> fileIndexList) {
        super(context);
        this.context = context;
        this.activity = (MainActivity) context;
        this.originalRect = originalRect;
        this.imageId = imageId;
        this.currentIndex = currentIndex;
        this.fileIndexList = fileIndexList;
        this.count = fileIndexList.size();
        setBackgroundColor(Color.TRANSPARENT);
        initView();
    }

    private void initView() {
        initCoverWindow();
        initAnimationIV();
        initHorizontalScrollView();
        initGridView();
        initProgressBar();
//        initPhotoView();
    }

    private void initCoverWindow() {
        windowManager = activity.getWindowManager();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = MyRect.getScreenWidth(context);
        layoutParams.height = MyRect.getScreenHeight(context);
        //FLAG_LAYOUT_IN_SCREEN
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;//全屏
        layoutParams.format = PixelFormat.RGBA_8888;//让背景透明，放大过程可以看到当前界面
        layoutParams.verticalMargin = 0;
        windowManager.addView(this, layoutParams);
    }

    private void initAnimationIV() {
        animationIV = new ImageView(context);
        animationIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams params = new LayoutParams(originalRect.getWidth(), originalRect.getHeight());
        params.leftMargin = originalRect.getLeft();
        params.topMargin = originalRect.getTop();
        addView(animationIV, params);//布局参数是点击的图片的布局参数，所以一开始animationIV是从点击的图片的位置开始的
        if (imageId instanceof Integer) {
            animationIV.setImageResource((Integer) imageId);
        } else {
            Glide.with(context).load((String) imageId)
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(animationIV);
        }
    }

    private PageHorizatalScrollView horizontalScrollView;

    private void initHorizontalScrollView() {
        horizontalScrollView = new PageHorizatalScrollView(context);
        LayoutParams hsLayoutParams = new LayoutParams(MyRect.getScreenWidth(context), MyRect.getScreenHeight(context));
        hsLayoutParams.leftMargin = 0;
        hsLayoutParams.topMargin = 0;
        addView(horizontalScrollView, hsLayoutParams);
        horizontalScrollView.mBaseScrollX = currentIndex * MyRect.getScreenWidth(context);
        horizontalScrollView.setOnScrollToIndexListen(new PageHorizatalScrollView.OnScrollToIndexListen() {
            @Override
            public void scrollToIndex(int index, int orientation) {
//                if (ftpFileList.get(index).getType() != FTPFile.TYPE_FILE) {//如果滑动到的不是文件，则接着滑
//                    if (currentIndex > index) {//判断是手右滑
//                        if (index != 0) {//不是第一个时，可以继续右滑
//                            horizontalScrollView.baseSmoothScrollTo(-MyRect.getScreenWidth(getContext()));
//                            horizontalScrollView.mBaseScrollX -= MyRect.getScreenWidth(getContext());
//                        } else {//是第一个时，左滑回来
//                            horizontalScrollView.baseSmoothScrollTo(MyRect.getScreenWidth(getContext()));
//                            horizontalScrollView.mBaseScrollX += MyRect.getScreenWidth(getContext());
//                        }
//                    } else if (currentIndex < index) {//手左滑
//                        if (index < ftpFileList.size() - 1) {//不是最后一个时，可以继续左滑
//                            horizontalScrollView.baseSmoothScrollTo(MyRect.getScreenWidth(getContext()));
//                            horizontalScrollView.mBaseScrollX += MyRect.getScreenWidth(getContext());
//                        } else {//是最后一个时，右滑回来
//                            horizontalScrollView.baseSmoothScrollTo(-MyRect.getScreenWidth(getContext()));
//                            horizontalScrollView.mBaseScrollX -= MyRect.getScreenWidth(getContext());
//                        }
//                    }
//                } else {//如果滑到的是文件

                if (currentIndex == count - 1 && currentIndex == index && orientation == PageHorizatalScrollView.NOT_SCROLL){//如果当前是最后一个，且滑动
                    Toast.makeText(context, "已经是最后一张", Toast.LENGTH_SHORT).show();
                } else if (currentIndex == 0 && currentIndex == index && orientation == PageHorizatalScrollView.NOT_SCROLL) {
                    Toast.makeText(context, "已经是第一张", Toast.LENGTH_SHORT).show();
                }

                if (currentIndex != index) {//如果滑动后与当前不是同一页
                    currentIndex = index;//拿到滚动位置
                    String path = activity.download(fileIndexList.get(currentIndex));
                    if (!path.equals("")) {
                        Glide.with(context).load(path)
//                                .skipMemoryCache(true) // 不使用内存缓存
//                                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                .into(animationIV);
                        adapter.notifyDataSetChanged();
                        gridView.setVisibility(VISIBLE);
                        progressBar.setVisibility(GONE);
                    } else {
                        gridView.setVisibility(INVISIBLE);
                        progressBar.setVisibility(VISIBLE);
                    }
                    activity.setOnGetPathListener(new MainActivity.OnGetPathListener() {
                        @Override
                        public void onGetPath(String path) {
                            adapter.notifyDataSetChanged();
                            gridView.setVisibility(VISIBLE);
                            progressBar.setVisibility(GONE);
                            Glide.with(context).load(path)
//                                    .skipMemoryCache(true) // 不使用内存缓存
//                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                                    .into(animationIV);
                        }
                    });
//                    originalRect = originalRects.get(currentIndex);
                }
                Log.e("TAG", "currentIndex" + currentIndex);
//                }
            }
        });

        gridViewBgView = new LinearLayout(context);
        LinearLayout.LayoutParams testParams = new LinearLayout.LayoutParams(MyRect.getScreenWidth(getContext()) * count, MyRect.getScreenHeight(context));
        horizontalScrollView.addView(gridViewBgView, testParams);
    }

    private ImageGridViewAdapter adapter;

    private void initGridView() {
        gridView = new GridView(context);
        gridView.setNumColumns(count);
        gridView.setColumnWidth(MyRect.getScreenWidth(context));
        LinearLayout.LayoutParams gridViewLayoutParams = new LinearLayout.LayoutParams(MyRect.getScreenWidth(getContext()) * count, MyRect.getScreenHeight(context));
        gridViewLayoutParams.leftMargin = 0;
        gridViewLayoutParams.topMargin = 0;
        gridViewBgView.addView(gridView, gridViewLayoutParams);
        adapter = new ImageGridViewAdapter(context, fileIndexList);
        gridView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ImageGridViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int i, View view) {
                startTransform(DialogImageView.STATE_TRANSFORM_OUT);
            }
        });
    }

    private ProgressBar progressBar;

    private void initProgressBar() {
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.image_loading_anim));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(progressBar, layoutParams);
        progressBar.setVisibility(GONE);
    }

    private void initPhotoView() {
        photoView = new PhotoView(getContext());
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setMinimumScale(0.5f);
        photoView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                startTransform(DialogImageView.STATE_TRANSFORM_OUT);
            }
        });
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(MyRect.getScreenWidth(getContext()), MyRect.getScreenHeight(getContext()));
        photoView.setLayoutParams(param);

        Glide.with(getContext()).load((String) imageId)
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(photoView);

        LayoutParams pvLayoutParams = new LayoutParams(MyRect.getScreenWidth(getContext()), MyRect.getScreenHeight(getContext()));
        pvLayoutParams.leftMargin = 0;
        pvLayoutParams.topMargin = 0;
        addView(photoView, pvLayoutParams);
    }

    /**
     * 因此View添加在Window，点击手机返回按钮无法响应，
     * 重写此方法可以处理点击手机返回的逻辑处理，缩小图片到原位置
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mState == DialogImageView.STATE_TRANSFORM_IN && isCanBack) {
                startTransform(DialogImageView.STATE_TRANSFORM_OUT);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 放大缩小动画
     *
     * @param state
     */
    public void startTransform(final int state) {
        isCanBack = false;
        final int duration = 300;
        valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (state == DialogImageView.STATE_TRANSFORM_IN) {
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            mState = DialogImageView.STATE_TRANSFORM_IN;
//            photoView.setVisibility(INVISIBLE);
            gridView.setVisibility(INVISIBLE);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", originalRect.getLeft(), 0);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", originalRect.getTop(), 0);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", originalRect.getWidth(), MyRect.getScreenWidth(getContext()));
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", originalRect.getHeight(), MyRect.getScreenHeight(getContext()));
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 0, 255);
            valueAnimator.setValues(leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        } else {
//            setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//            photoView.setVisibility(INVISIBLE);//设置不可见，
            gridView.setVisibility(INVISIBLE);
            animationIV.setVisibility(VISIBLE);
            setBackgroundColor(Color.TRANSPARENT);
            mState = DialogImageView.STATE_TRANSFORM_OUT;
//            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom.endScale, mTransfrom.startScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", animationIV.getLeft(), originalRect.getLeft());
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", animationIV.getTop(), originalRect.getTop());
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", animationIV.getWidth(), originalRect.getWidth());
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", animationIV.getHeight(), originalRect.getHeight());
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 255, 0);
            valueAnimator.setValues(leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public synchronized void onAnimationUpdate(ValueAnimator animation) {
                //                mTransfrom.scale = (Float) animation.getAnimatedValue("scale");
                Float left = (Float) animation.getAnimatedValue("left");
                Float top = (Float) animation.getAnimatedValue("top");
                Float width = (Float) animation.getAnimatedValue("width");
                Float height = (Float) animation.getAnimatedValue("height");
                Integer mBgAlpha = (Integer) animation.getAnimatedValue("alpha");

                LayoutParams layoutParams = new LayoutParams(width.intValue(), height.intValue());
                layoutParams.leftMargin = left.intValue();
                layoutParams.topMargin = top.intValue();
                animationIV.setLayoutParams(layoutParams);
                setAlpha(mBgAlpha);
            }
        });
        final CoverWindow[] coverWindows = {this};
        valueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /*
                 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了
                 * ， 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
                 */

//                Float left = (Float) ((ValueAnimator)animation).getAnimatedValue("left");
//                Float top = (Float) ((ValueAnimator)animation).getAnimatedValue("top");
//                Float width = (Float) ((ValueAnimator)animation).getAnimatedValue("width");
//                Float height = (Float) ((ValueAnimator)animation).getAnimatedValue("height");
//                Integer mBgAlpha = (Integer) ((ValueAnimator)animation).getAnimatedValue("alpha");
//
//                LayoutParams layoutParams = new LayoutParams(width.intValue(), height.intValue());
//                layoutParams.leftMargin = left.intValue();
//                layoutParams.topMargin = top.intValue();
//                animationIV.setLayoutParams(layoutParams);
//                setAlpha(mBgAlpha);


                // TODO 这个可以根据实际需求来修改
                if (mState == DialogImageView.STATE_TRANSFORM_IN) {
                    horizontalScrollView.baseSmoothScrollTo(0, PageHorizatalScrollView.SCROLL);
                    setBackgroundColor(Color.BLACK);//动画完成后，设置背景为黑色
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
//                            photoView.setVisibility(VISIBLE);
                            gridView.setVisibility(VISIBLE);
                            animationIV.setVisibility(INVISIBLE);//动画中的图片不可见

                        }
                    };
                    handler.sendEmptyMessageDelayed(0, duration);
                    isCanBack = true;
                } else if (mState == DialogImageView.STATE_TRANSFORM_OUT) {
                    gridViewBgView.removeView(gridView);
                    gridView = null;
                    horizontalScrollView.removeView(gridViewBgView);
                    gridViewBgView = null;
//                    removeView(animationIV);
//                    animationIV = null;
//                    removeView(photoView);
//                    photoView = null;
                    windowManager.removeView(coverWindows[0]);
                    coverWindows[0] = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCanBack = true;
            }
        });
        valueAnimator.start();
    }

}
