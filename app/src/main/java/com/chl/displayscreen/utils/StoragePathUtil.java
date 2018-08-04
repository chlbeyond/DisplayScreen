package com.chl.displayscreen.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2018/8/3.
 */

public class StoragePathUtil {

    public static String getPaht(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//SD卡挂载，存在SD卡中
            return String.valueOf(context.getExternalCacheDir());
        }
        return String.valueOf(context.getCacheDir());
    }

    public static void clearAppCache(Context context){//清除应用所有缓存
        //TODO 请补充
        File cache = context.getCacheDir();
        deleteCache(cache);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalCache = context.getExternalCacheDir();
            deleteCache(externalCache);
        }
        Toast.makeText(context, "已清除缓存", Toast.LENGTH_SHORT).show();
    }

    private static void deleteCache(File file) {
        if (file != null && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    deleteCache(files[i]);
                }
            }
        }
        file.delete();
    }
}
