package com.chl.displayscreen.commonView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chl.displayscreen.commonView.DialogImageView.DialogImageView;

import java.util.List;

/**
 * Created by public1 on 2017/5/23.
 */

public class TNinePlaceGridView extends GridView {

    final static int itemGap = 10;//item间隙
    //这个没用
    int itemWidth = (TRect.getScreenWidth(getContext()) - 68*2 - itemGap*2)/3;

    final static int numColumns = 6;//列数

    private List<Object> imageNames;
    private GridViewAdapter gridViewAdapter;

    public List<Object> getImageNames() {
        return imageNames;
    }

    public void setImageNames(List<Object> imageNames) {
        this.imageNames = imageNames;

        int rowNum = imageNames.size()/numColumns;
        if (imageNames.size() % numColumns == 0) {

        } else  {
            rowNum++;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        int width = getWidth();
        Log.e("TAG","setImageNames getWidth():" + width);
        itemWidth = (width - numColumns*itemGap)/numColumns;
        setColumnWidth(itemWidth);
        layoutParams.height = rowNum*itemWidth + (rowNum - 1)*itemGap;
        setLayoutParams(layoutParams);
        if (gridViewAdapter != null) {
            gridViewAdapter.imageNames = this.imageNames;
            gridViewAdapter.notifyDataSetChanged();
        }
    }

    public TNinePlaceGridView(Context context) {
        super(context);
        init();
    }

    public TNinePlaceGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TNinePlaceGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        Log.e("TAG","init getWidth():" + getWidth());
//        setBackgroundColor(Color.RED);
        setNumColumns(numColumns);
        setHorizontalSpacing(itemGap);
        setVerticalSpacing(itemGap);
//        setColumnWidth(itemWidth);
        gridViewAdapter = new GridViewAdapter(getContext());
        gridViewAdapter.ninePlaceGridView = this;
        setAdapter(gridViewAdapter);

        //增加整体布局监听
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (imageNames == null) return true;
                int width = getWidth();
                //
                Log.e("TAG","onPreDraw getWidth():" + getWidth());
                itemWidth = (width - numColumns*itemGap)/numColumns;
                setColumnWidth(itemWidth);
                //
                int rowNum = imageNames.size()/numColumns;
                if (imageNames.size() % numColumns == 0) {

                } else  {
                    rowNum++;
                }
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                layoutParams.height = rowNum*itemWidth + (rowNum - 1)*itemGap;
                setLayoutParams(layoutParams);
                gridViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    //////////////////

    public class GridViewAdapter extends BaseAdapter {

        private Context context;
        public List<Object> imageNames;
        public TNinePlaceGridView ninePlaceGridView;

        public GridViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (imageNames != null) return imageNames.size();
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = new DialogImageView(context);
                DialogImageView imageView = (DialogImageView) view;
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LayoutParams param = new LayoutParams(itemWidth, itemWidth);
                view.setLayoutParams(param);
            }

            DialogImageView imageView = (DialogImageView) view;
            Object imgURL = imageNames.get(i);
//            Picasso.with(context).load(imgURL).into(imageView);
            if (imgURL instanceof Integer) {
                imageView.setImageResource((Integer) imgURL);
            } else {
                Glide.with(getContext()).load((String) imgURL).into(imageView);
            }
            imageView.imageId = imgURL;
            return view;
        }
    }
}
