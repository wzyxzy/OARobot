package com.zgty.oarobot.util;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zy on 2017/12/6.
 * 数据表导出
 */

public class DBMakeUtil {
    public static File ExportToCSV(Cursor c, String fileName) {

        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        BufferedWriter bfw;
        File sdCardDir = Environment.getExternalStorageDirectory();
        File saveFile = new File(sdCardDir + "/oa_robot", fileName);
        if (saveFile.getParentFile()==null){
            saveFile.getParentFile().mkdirs();// 创建文件夹
        }
        try {

            rowCount = c.getCount();
            colCount = c.getColumnCount();
            fw = new FileWriter(saveFile);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                c.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(c.getColumnName(i) + ',');
                    else
                        bfw.write(c.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);
                    // Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
                    // Toast.LENGTH_SHORT).show();
                    Log.v("导出数据", "正在导出第" + (i + 1) + "条");
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1)
                            bfw.write(c.getString(j) == null ? "" : c.getString(j) + ',');
                        else
                            bfw.write(c.getString(j) == null ? "" : c.getString(j));
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            // Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
            Log.v("导出数据", "导出完毕！");
            return saveFile;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            c.close();
        }
    }
}
