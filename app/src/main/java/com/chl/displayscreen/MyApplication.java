package com.chl.displayscreen;

import android.app.Application;

import com.chl.displayscreen.utils.LogToFile;

/**
 * Created by Administrator on 2018/8/3.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogToFile.init(MyApplication.this);
    }
}
