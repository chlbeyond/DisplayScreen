package com.chl.displayscreen.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chl.displayscreen.R;
import com.chl.displayscreen.commonView.DialogImageView.DialogImageView;
import com.chl.displayscreen.commonView.DialogImageView.MyGridView;
import com.chl.displayscreen.commonView.ProgressView.DonutProgress;
import com.chl.displayscreen.database.ormHelper.PhotoHelper;
import com.chl.displayscreen.database.ormHelper.PhotoInfo;
import com.chl.displayscreen.utils.DataComparatorUtils;
import com.chl.displayscreen.utils.LogToFile;
import com.chl.displayscreen.utils.OpenOtherFile;
import com.chl.displayscreen.utils.StoragePathUtil;
import com.chl.displayscreen.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;

public class MainActivity extends CheckPermissionsActivity {

    /**
     * 只需要ip地址，不需要前面的ftp://
     */
    private static final String HOST = "192.168.1.92";
    private static final int PORT = 21;
    private String USERNAME = "administrator";
    private String PASSWORD = "123456";
    private TextView path_tv;
    private LinearLayout dissConnect_ll;
    private MyGridView gridView;
    //    private GridViewAdapter gridViewAdapter;
    private FTPClient client;
    private List<FTPFile> ftpFileList = new ArrayList<>();
    public String currentPath = "/";//当前路径

    int myPosition = 0;
    private LinearLayout linearLayout;

    private List<Integer> fileIndexList = new ArrayList<>();


    public static final int BACK = R.mipmap.back;
    private static final int SUCCESS = 0x001;
    private static final int FAIL = 0x002;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCCESS) {
                path_tv.setText(currentPath);
//                gridViewAdapter.notifyDataSetChanged();
                gridView.setImageNames(ftpFileList);
                getFileIndexList();
                dissConnect_ll.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            } else if (msg.what == FAIL) {
                Toast.makeText(MainActivity.this,
                        "连接失败", Toast.LENGTH_SHORT)
                        .show();
                refreshTip_tv.setText("无法连接服务器，请刷新重试");
                donut_progress.setVisibility(View.GONE);
                stopTimer();
                dissConnect_ll.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private TextView refreshTip_tv;
    private DonutProgress donut_progress;

    private void initView() {
        path_tv = (TextView) findViewById(R.id.path_tv);
        dissConnect_ll = (LinearLayout) findViewById(R.id.dissConnect_ll);
        refreshTip_tv = (TextView)findViewById(R.id.refreshTip_tv);
        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
//        gridView = (GridView) findViewById(R.id.gridView);
        initMyGridView();
        init();
        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoragePathUtil.clearAppCache(MainActivity.this);
            }
        });
    }

    private void initMyGridView() {
        gridView = (MyGridView) findViewById(R.id.gridView);
        gridView.setImageNames(ftpFileList);
    }

    private void init() {
        dissConnect_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTip_tv.setText("正在重连，请耐心等待...");
                initData();
            }
        });

//        gridViewAdapter = new GridViewAdapter(MainActivity.this, ftpFileList);
//        gridViewAdapter.gridView = gridView;
//        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                gridView.setEnabled(false);//防多次点击
                linearLayout = (LinearLayout) view;
                handleItemClick(position);

            }
        });
//        gridViewAdapter.setOnItemClickListener(new GridViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                imageView = (DialogImageView) view;
//                handleItemClick(position);
//            }
//        });
    }

    private void handleItemClick(final int position) {
        if (ftpFileList.get(position).getType() == FTPFile.TYPE_DIRECTORY) {//如果点击的是目录
            //目录
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!client.currentDirectory().equals("/")) {//判断是否跟路径
                            currentPath = client.currentDirectory() + "/" + ftpFileList.get(position).getName();//点击后的路径
                        } else {
                            currentPath = "/" + ftpFileList.get(position).getName();//点击后的路径
                        }
                        changeDirectory(currentPath);
                    } catch (Exception e) {
                        Log.e("TAG", "TYPE_DIRECTORY: " + e);
                        handler.sendEmptyMessage(FAIL);
                    }
                }
            }).start();
        } else if (ftpFileList.get(position).getType() == BACK) {//点击返回上一层
            backUp();

        } else if (ftpFileList.get(position).getType() == FTPFile.TYPE_FILE) {//是文件就下载
            isOnClick = true;
            String path = download(position);
            if (!path.equals("")) {//是否已经下载
                if (isOnClick) {//通过点击图片看大图
                    if (OpenOtherFile.isMatchImage(path)) {//如果是图片
                        DialogImageView imageView = (DialogImageView) linearLayout.getChildAt(0);
                        imageView.showImageToWindow(path, transformPosition(position), fileIndexList);
                    } else {
                        OpenOtherFile.openFileByPath(MainActivity.this, path);
                    }
                    isOnClick = false;
                }
            }
            else {//如果还没下载
               donut_progress.setVisibility(View.VISIBLE);
            }
        }
    }

    private void backUp() {//返回上一级
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.changeDirectoryUp();//返回上一级目录
                    currentPath = client.currentDirectory();
                    changeDirectory(currentPath);
                } catch (Exception e) {
                    Log.e("TAG", "BACK: " + e);
                    handler.sendEmptyMessage(FAIL);
                }
            }
        }).start();
    }

    private boolean isOnClick = false;
    private long fileSize;

    //判断是否已经下载，如果已经下载，则将路径返回，否则返回"",但不会去下载
    public String isDownload(int position) {
        String dir = StoragePathUtil.getPaht(MainActivity.this)
                + currentPath + "/";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String path = dir + ftpFileList.get(position).getName();
        File file = new File(path);

        if (file.exists() && file.isFile()) {//文件已经存在，可能已经下载
            Log.e("exists", "original file exists" + transformPosition(position));

            PhotoInfo info = PhotoHelper.getInstance(MainActivity.this).queryByName(path);//根据路径查询该图片信息

            if (info != null) {
                Log.e("exists", "original info exists");
                long infoData = info.getModifiedDate().getTime();
                long fileData = ftpFileList.get(position).getModifiedDate().getTime();

                if (infoData == fileData) {//如果两个文件时间相等，则是同一张图片，不用下载
                    Log.e("exists", "original file exists:\n" + "infoData:" + infoData + "\nfileData" + fileData);
                    return path;
                }
            }
        }

        return "";
    }

    public String download(final int position) {
//        while (position < ftpFileList.size() && ftpFileList.get(position).getType() != FTPFile.TYPE_FILE) {
//            position += 1;
//        }
//        if (position != ftpFileList.size()) {
        String dir = StoragePathUtil.getPaht(MainActivity.this)
                + currentPath + "/";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        final String path = dir + ftpFileList.get(position).getName();
        myPosition = position;

        final File file = new File(path);
        if (file.exists() && file.isFile()) {//文件已经存在，可能已经下载
            Log.e("exists", "original file exists" + transformPosition(position));

            PhotoInfo info = PhotoHelper.getInstance(MainActivity.this).queryByName(path);//根据路径查询该图片信息

            if (info != null) {
                Log.e("exists", "original info exists");
                long infoData = info.getModifiedDate().getTime();
                long fileData = ftpFileList.get(position).getModifiedDate().getTime();

                if (infoData == fileData) {//如果两个文件时间相等，则是同一张图片，不用下载
                    Log.e("exists", "original file exists:\n" + "infoData:" + infoData + "\nfileData" + fileData);
                    return path;
                }
            }
            Log.e("TAG", "file.delete" + file.delete());//不是同一张图片就删除，再下载
        }

        final String filePath;//文件在远程的路径
        if (currentPath.equals("/")) {
            filePath = "/" + ftpFileList.get(position).getName();
        } else {
            filePath = currentPath + "/" + ftpFileList.get(position).getName();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fileSize = client.fileSize(filePath);
                    client.download(ftpFileList.get(position).getName(),
                            file, new MyTransferListener());
                } catch (Exception e) {
                    Log.e("TAG", "download: " + e);
                    handler.sendEmptyMessage(FAIL);
//                    Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
//                    donut_progress.setVisibility(View.GONE);
                }
            }
        }).start();
//        }
        return "";
    }

    //获取传过来的路径下的数据
    private void changeDirectory(String currentPath) {
        try {
            client.changeDirectory(currentPath);
            FTPFile[] ftpFiles = client.list();
            ftpFileList.clear();
            for (int i = 0; i < ftpFiles.length; i++) {
                ftpFileList.add(ftpFiles[i]);
            }
            Collections.sort(ftpFileList, DataComparatorUtils.comparator);//排序
            if (!currentPath.equals("/")) {//如果不是获取根路径下的数据，那么必然有返回键
                FTPFile ftpFile = new FTPFile();
                ftpFile.setType(BACK);
                ftpFile.setName("返回上一级");
                ftpFileList.add(0, ftpFile);
            }
            handler.sendEmptyMessage(SUCCESS);
        } catch (Exception e) {
            Log.e("TAG", "changeDirectory: " + e);
            handler.sendEmptyMessage(FAIL);
        }
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    if (client == null) {//如果已经连接过了，就不能再连接
                    client = new FTPClient();
                    client.connect(HOST, PORT);
                    client.login(USERNAME, PASSWORD);
                    Log.e("TAG", "登录成功");
//                    }
//                    ftpFiles = client.list();
//                    ftpFileList.clear();
//                    for (int i = 0; i < ftpFiles.length; i++) {
//                        ftpFileList.add(ftpFiles[i]);
//                    }
//                    currentPath = "/";
                    changeDirectory(currentPath);
                    startTimer();//开启轮询
                    handler.sendEmptyMessage(SUCCESS);
                } catch (Exception e) {
                    Log.e("TAG", "initData: " + e);
                    handler.sendEmptyMessage(FAIL);
                }
            }
        }).start();
    }

    private long hadDowloadSize = 0;
    public class MyTransferListener implements FTPDataTransferListener {
        public void started() {
            Log.i("download", "download start");
        }

        public void transferred(int length) {
            Log.i("download", "download " + length + " bytes");

            hadDowloadSize = hadDowloadSize + length;
            final double progressSize = hadDowloadSize * 100 / fileSize;

            final String progString = String.format("%.2f",progressSize);

            Log.i("download", "have download " + progressSize + " %");
            Log.i("download", "have " + hadDowloadSize);
            Log.i("download", "fileSize " + fileSize);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    donut_progress.setText(progString + "%");
                    donut_progress.setProgress((float) progressSize);
                    donut_progress.setShowText(true);
                }
            });

        }

        public void completed() {//图片下载完成的回调
            Log.e("download", "download completed" + transformPosition(myPosition));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hadDowloadSize = 0;
                    donut_progress.setText(0.0 + "%");
                    donut_progress.setProgress(0);
                    donut_progress.setVisibility(View.GONE);
                }
            });
            String dir = StoragePathUtil.getPaht(MainActivity.this)
                    + currentPath + "/";
            final String path = dir + ftpFileList.get(myPosition).getName();

            PhotoInfo info = new PhotoInfo();
            info.setName(path);
            info.setModifiedDate(ftpFileList.get(myPosition).getModifiedDate());
            info.setType(ftpFileList.get(myPosition).getType());
            PhotoHelper.getInstance(MainActivity.this).save(info);//下载完成后保存数据


            if (!OpenOtherFile.isMatchImage(path)) {//如果不是图片
                OpenOtherFile.openFileByPath(MainActivity.this, path);
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (isOnClick) {//通过点击图片看大图，需要CoverWindow对象，如果是滑动则不需要
                            DialogImageView imageView = (DialogImageView) linearLayout.getChildAt(0);
                            imageView.showImageToWindow(path, transformPosition(myPosition), fileIndexList);
                            isOnClick = false;
                        }

                        gridView.notifyDataSetChanged();//图片下载后就刷新界面

                        if (onGetPathListener != null) {
                            onGetPathListener.onGetPath(path);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "图片放大", Toast.LENGTH_SHORT).show();
                        LogToFile.e("下载后图片放大:" ,"下载后图片放大:" + e);
                    }
                }
            });
        }

        public void aborted() {
            Log.i("download", "download aborted");
        }

        public void failed() {
            Log.i("download", "download failed");
        }
    }

    private List<Integer> getFileIndexList() {//获取是文件且后缀是图片的下标
        fileIndexList.clear();
        for (int i = 0; i < ftpFileList.size(); i++) {
            if (ftpFileList.get(i).getType() == FTPFile.TYPE_FILE) {
                if (OpenOtherFile.isMatchImage(ftpFileList.get(i).getName())) {//后缀是否图片
                    fileIndexList.add(i);
                }
            }
        }
        return fileIndexList;
    }

    private int transformPosition(int position) {
        return fileIndexList.indexOf(position);
    }

    /**
     * 定时任务工具类
     */
    public static Timer timer = new Timer();
    private Task timerTask = new Task();
    /**
     * 轮询时间间隔
     */
    public static int MLOOP_INTERVAL_SECS = 2 * 60;

    /**
     * 启动轮询
     */
    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new Task();
        }
        timer.schedule(timerTask, MLOOP_INTERVAL_SECS * 1000, MLOOP_INTERVAL_SECS * 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    class Task extends TimerTask {
        @Override
        public void run() {
            Log.i("TAG", "执行轮询操作...");
            if (client != null && client.isConnected()) {
                try {
                    client.currentDirectory();
                } catch (Exception e) {
                    Log.e("TAG", "TimerException: " + e);
                    handler.sendEmptyMessage(FAIL);
                }
            } else {
                Log.e("TAG", "isConnected: " + client.isConnected());
            }
        }
    }

    private OnGetPathListener onGetPathListener;

    public void setOnGetPathListener(OnGetPathListener onGetPathListener) {
        this.onGetPathListener = onGetPathListener;
    }

    public interface OnGetPathListener {
        void onGetPath(String path);
    }

    // 第一次按下返回键的时间
    private long firstPressedTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//点击返回键
            if ("/".equals(currentPath)) {
                if (System.currentTimeMillis() - firstPressedTime < 2000) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    firstPressedTime = System.currentTimeMillis();
                }
            } else {
                backUp();//不是根目录下就返回上一级
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
