package com.chl.displayscreen.commonView.DialogImageView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chl.displayscreen.R;
import com.chl.displayscreen.activity.MainActivity;
import com.chl.displayscreen.utils.OpenOtherFile;
import com.chl.displayscreen.utils.StoragePathUtil;
import com.chl.displayscreen.utils.Utils;

import java.io.File;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPFile;

/**
 * Created by Administrator on 2018/7/31.
 */

public class MyGridView extends GridView {

    final static int itemGap = 10;//item间隙
    final static int numColumns = 3;//列数

    //这个没用
    int itemWidth = MyRect.getScreenWidth(getContext());

    //    private List<Object> imageNames;
    private GridViewAdapter gridViewAdapter;
    private List<FTPFile> ftpFileList;

//    public List<Object> getImageNames() {
//        return imageNames;
//    }

    public void notifyDataSetChanged() {
        gridViewAdapter.notifyDataSetChanged();
    }

    public void setImageNames(List<FTPFile> ftpFileList) {
        this.ftpFileList = ftpFileList;

        int rowNum = ftpFileList.size() / numColumns;
        if (ftpFileList.size() % numColumns == 0) {

        } else {
            rowNum++;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();//获取到的是MyGridView的父布局
        int width = getWidth();
        Log.e("TAG", "setImageNames getWidth():" + width);
        itemWidth = (width - numColumns * itemGap) / numColumns;
        setColumnWidth(itemWidth);
//        layoutParams.height = rowNum * itemWidth + (rowNum - 1) * itemGap;
//        setLayoutParams(layoutParams);
        if (gridViewAdapter != null) {
            gridViewAdapter.ftpFileList = this.ftpFileList;
            gridViewAdapter.notifyDataSetChanged();
        }
    }

    private Context context;
    private MainActivity activity;

    public MyGridView(Context context) {
        super(context);
        this.context = context;
        this.activity = (MainActivity) context;
        init();
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.activity = (MainActivity) context;
        init();
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.activity = (MainActivity) context;
        init();
    }

    private void init() {
        Log.e("TAG", "init getWidth():" + getWidth());
//        setBackgroundColor(Color.RED);
        setNumColumns(numColumns);
        setHorizontalSpacing(itemGap);
        setVerticalSpacing(itemGap);
//        setColumnWidth(itemWidth);
        gridViewAdapter = new GridViewAdapter(getContext());
        gridViewAdapter.myGridView = this;
        setAdapter(gridViewAdapter);

        //增加整体布局监听
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (ftpFileList == null) return true;
                int width = getWidth();
                //
                Log.e("TAG", "onPreDraw getWidth():" + getWidth());
                itemWidth = (width - numColumns * itemGap) / numColumns;
                setColumnWidth(itemWidth);
                //
                int rowNum = ftpFileList.size() / numColumns;
                if (ftpFileList.size() % numColumns == 0) {

                } else {
                    rowNum++;
                }
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
//                int height = rowNum * itemWidth + (rowNum) * itemGap;
//                if (height > layoutParams.height) {
//                    layoutParams.height = height;
//                    setLayoutParams(layoutParams);
//                }
//                layoutParams.height = rowNum*itemWidth + (rowNum)*itemGap;
                gridViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    //////////////////

    public class GridViewAdapter extends BaseAdapter {

        private Context context;
        private MainActivity activity;
        public List<FTPFile> ftpFileList;
        public MyGridView myGridView;

        public GridViewAdapter(Context context) {
            this.context = context;
            this.activity = (MainActivity) context;
        }

        @Override
        public int getCount() {
            if (ftpFileList != null) return ftpFileList.size();
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
//            if (view == null) {
//                view = new DialogImageView(context);
//                DialogImageView imageView = (DialogImageView) view;
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                LayoutParams param = new LayoutParams(itemWidth, itemWidth);
//                view.setLayoutParams(param);
//            }
//
//            DialogImageView imageView = (DialogImageView) view;
//            Object imgURL = ftpFileList.get(i);
////            Picasso.with(context).load(imgURL).into(imageView);
//            if (imgURL instanceof Integer) {
//                imageView.setImageResource((Integer) imgURL);
//            } else {
//                Glide.with(getContext()).load((String) imgURL).into(imageView);
//            }
//            imageView.imageId = imgURL;
//            return view;


            ViewHold viewHold;

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.grid_item, null);
                viewHold = new ViewHold(view);
                view.setTag(viewHold);
            } else {
                viewHold = (ViewHold) view.getTag();
            }

            FTPFile ftpFile = ftpFileList.get(i);
            viewHold.photoName_tv.setText(Utils.getFileNameNoEx(ftpFile.getName()));
            if (ftpFile.getType() == FTPFile.TYPE_DIRECTORY) {
                Glide.with(context).load(R.mipmap.folder).into(viewHold.imageView);
            } else if (ftpFile.getType() == activity.BACK) {
                Glide.with(context).load(R.mipmap.back).into(viewHold.imageView);
            } else if (ftpFile.getType() == FTPFile.TYPE_FILE) {
                String path = getPhoto(ftpFile.getName());//如果图片存在，则显示图片
                if (path.equals("file")) {
                    Glide.with(context).load(R.mipmap.file)
                            .into(viewHold.imageView);
                } else if (!path.equals("")) {
                    Glide.with(context).load(path)
                            .skipMemoryCache(true) // 不使用内存缓存
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                            .into(viewHold.imageView);
                } else {
                    Glide.with(context).load(R.mipmap.photo)
                            .into(viewHold.imageView);
                }
            }
//        viewHold.imageView.originalRects = ftpFile.getName();
            viewHold.imageView.gridView = myGridView;
            return view;
        }

    }

    private String getPhoto(String path) {
        String dir = StoragePathUtil.getPaht(context)
                + activity.currentPath + "/" + path;
        File file = new File(dir);
      if (!OpenOtherFile.isMatchImage(dir)) {//如果匹配不了图片，则展示文件形式
            dir = "file";
        } else if (!file.exists() || !file.isFile()) {//如果图片不存在，则显示默认图片
            dir = "";
        }
        return dir;
    }

        private class ViewHold {
        public DialogImageView imageView;
        public TextView photoName_tv;

        public ViewHold(View itemView) {
            imageView = itemView.findViewById(R.id.dialogImageView);
            photoName_tv = itemView.findViewById(R.id.photoName_tv);
        }
    }
}
