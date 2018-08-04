package com.chl.displayscreen.utils;

import java.util.Comparator;

import it.sauronsoftware.ftp4j.FTPFile;

/**
 * Created by Administrator on 2018/8/2.
 */

public class DataComparatorUtils {

    //排序，将文件夹与文件分开
    public static Comparator<FTPFile> comparator = new Comparator<FTPFile>() {
        public int compare(FTPFile f1, FTPFile f2) {
            if (f1 == null || f2 == null) {// 先比较null
                if (f1 == null) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                if (f1.getType() == FTPFile.TYPE_DIRECTORY && f2.getType() == FTPFile.TYPE_DIRECTORY) { // 再比较文件夹
                    return f1.getName().compareToIgnoreCase(f2.getName());
                } else {
                    if (f1.getType() == FTPFile.TYPE_DIRECTORY && f2.getType() == FTPFile.TYPE_FILE) {
                        return -1;
                    } else if (f2.getType() == FTPFile.TYPE_DIRECTORY && f1.getType() == FTPFile.TYPE_FILE) {
                        return 1;
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName());// 最后比较文件
                    }
                }
            }
        }
    };
}
