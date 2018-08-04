package com.chl.displayscreen.commonView.DialogImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chl.displayscreen.activity.MainActivity;
import com.chl.displayscreen.commonView.photoview.OnViewTapListener;
import com.chl.displayscreen.commonView.photoview.PhotoView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/30.
 */

public class ImageGridViewAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    //    public List<Object> imageIds;
    private List<Integer> fileIndexList;

    public ImageGridViewAdapter(Context context, List<Integer> fileIndexList) {
        this.context = context;
        this.activity = (MainActivity) context;
        this.fileIndexList = fileIndexList;
    }

    @Override
    public int getCount() {
        return this.fileIndexList.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        PhotoView imageView;
        if (view == null) {
            view = new PhotoView(context);
            imageView = (PhotoView) view;
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setMinimumScale(0.5f);
            imageView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(i, view);
                    }
                }
            });
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(MyRect.getScreenWidth(context), MyRect.getScreenHeight(context));
            imageView.setLayoutParams(param);
        }else {
            imageView = (PhotoView) view;
        }
//        Picasso.with(context).load("http://ww2.sinaimg.cn/mw690/9e6995c9gw1f2uu70bzohj209q06g3yw.jpg").into(imageView);
//        if (imageIds.get(i) instanceof Integer) {
//            imageView.setImageResource((Integer) imageIds.get(i));
//        } else {
//            Glide.with(context).load((String) imageIds.get(i)).into(imageView);
//        }
        String path = activity.isDownload(fileIndexList.get(i));
        if (!path.equals("")) {//是否已经下载
            Glide.with(context).load(path)
//                    .skipMemoryCache(true) // 不使用内存缓存
//                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(imageView);
        }

        return imageView;
    }


    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int i, View view);
    }
}

