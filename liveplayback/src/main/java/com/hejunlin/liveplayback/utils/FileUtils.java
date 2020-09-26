package com.hejunlin.liveplayback.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 */
public class FileUtils {
    //先定义
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    //然后通过一个函数来申请
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // https://www.jianshu.com/p/6abb3dbe0c8b
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        LogC.e("state=", "" + state);
        return false;
    }

    // https://www.cnblogs.com/xiobai/p/10839494.html

    /**
     * 【检查文件目录是否存在，不存在就创建新的目录】
     **/
    public static void checkFilePath(File file, boolean isDir) {
        if (file != null) {
            if (!isDir) {     //如果是文件就返回父目录
                file = file.getParentFile();
            }
            if (file != null && !file.exists()) {
                file.mkdirs();
            }
        }
    }

    public static String path = null;

    static {
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath()+ File.separator;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 【创建文件】
     **/
    public static void addFile(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File newFile = new File(sdCard.getCanonicalPath() + File.separator  + fileName);
                if (!newFile.exists()) {
                    boolean isSuccess = newFile.createNewFile();
                    LogC.i("TAG:", "文件创建状态--->" + isSuccess);
                    LogC.i("TAG:", "文件所在路径：" + newFile.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 【删除文件】
     **/
    public static void deleteFile(File file) {
        if (file.exists()) {                          //判断文件是否存在
            if (file.isFile()) {                      //判断是否是文件
                boolean isSucess = file.delete();
                LogC.i("TAG:", "文件删除状态--->" + isSucess);
            } else if (file.isDirectory()) {           //判断是否是文件夹
                File files[] = file.listFiles();    //声明目录下所有文件
                for (int i = 0; i < files.length; i++) {   //遍历目录下所有文件
                    deleteFile(files[i]);           //把每个文件迭代删除
                }
                boolean isSucess = file.delete();
                LogC.i("TAG:", "文件夹删除状态--->" + isSucess);
            }
        }
    }
}
