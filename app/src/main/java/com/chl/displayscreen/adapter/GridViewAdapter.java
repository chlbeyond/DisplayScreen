package com.chl.displayscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chl.displayscreen.R;
import com.chl.displayscreen.activity.MainActivity;
import com.chl.displayscreen.commonView.DialogImageView.DialogImageView;
import com.chl.displayscreen.commonView.DialogImageView.MyGridView;

import java.util.List;

import it.sauronsoftware.ftp4j.FTPFile;

/**
 * Created by Administrator on 2018/7/28.
 */

public class GridViewAdapter extends BaseAdapter {

    private MainActivity activity;
    private Context mContext;
    private List<FTPFile> ftpFileList;
    public MyGridView gridView;

    public GridViewAdapter(Context mContext, List<FTPFile> ftpFileList) {
        this.activity = (MainActivity)mContext;
        this.mContext = mContext;
        this.ftpFileList = ftpFileList;
    }

    @Override
    public int getCount() {
        return ftpFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return ftpFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            viewHold = new ViewHold(view);
            view.setTag(viewHold);
        } else {
            view = convertView;
            viewHold = (ViewHold) view.getTag();
        }

        FTPFile ftpFile = ftpFileList.get(position);
        viewHold.photoName_tv.setText(ftpFile.getName());
        if (ftpFile.getType() == FTPFile.TYPE_DIRECTORY) {
            Glide.with(mContext).load(R.mipmap.folder).into(viewHold.imageView);
        } else if (ftpFile.getType() == activity.BACK) {
            Glide.with(mContext).load(R.mipmap.back).into(viewHold.imageView);
        } else if (ftpFile.getType() == FTPFile.TYPE_FILE) {
            Glide.with(mContext).load(R.mipmap.file).into(viewHold.imageView);
        }
//        viewHold.imageView.originalRects = ftpFile.getName();
        viewHold.imageView.gridView = gridView;
        return view;
    }

    private class ViewHold {
//        public View itemView;
        public DialogImageView imageView;
        public TextView photoName_tv;

        public ViewHold(View itemView) {
//            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.dialogImageView);
            photoName_tv = itemView.findViewById(R.id.photoName_tv);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onItemClickListener != null) {
//                        onItemClickListener.onItemClick(v, (Integer) itemView.getTag());
//                    }
//                }
//            });
        }
    }

//    private OnItemClickListener onItemClickListener;
//
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
}
