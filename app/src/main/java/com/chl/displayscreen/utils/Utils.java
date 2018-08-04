package com.chl.displayscreen.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

/**
 * Created by Administrator on 2018/8/2.
 */

public class Utils {
    /*
 * Java文件操作 获取不带扩展名的文件名
 * */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * try get host activity from view.
     * views hosted on floating window like dialog and toast will sure return null.
     * @return host activity; or null if not available
     */
    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
