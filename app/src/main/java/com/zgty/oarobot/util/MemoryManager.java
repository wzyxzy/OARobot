package com.zgty.oarobot.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zy on 2018/2/28.
 */

public class MemoryManager {

    // 获取android当前可用内存大小
    public String getAvailMemory(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        assert am != null;
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    //系统总内存大小
    public String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    //ROM大小
    public String getRomSpace(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        //long usedBlocks=totalBlocks-availableBlocks;

        String totalSize = Formatter.formatFileSize(context, totalBlocks * blockSize);
        String availableSize = Formatter.formatFileSize(context, availableBlocks * blockSize);

        return availableSize + "/" + totalSize;
    }


    //得到SD卡大小
    public String getSdcardSize(Context context) {

        File path = Environment.getExternalStorageDirectory();//得到SD卡的路径
        StatFs stat = new StatFs(path.getPath());//创建StatFs对象，用来获取文件系统的状态
        long blockCount = stat.getBlockCount();
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        String totalSize = Formatter.formatFileSize(context, blockCount * blockSize);//格式化获得SD卡总容量
        String availableSize = Formatter.formatFileSize(context, availableBlocks * blockSize);//获得SD卡可用容

        return availableSize + "/" + totalSize;
    }
}
