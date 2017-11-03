package com.zgty.oarobot.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * toast处理
 */

public class LogToastUtils {
    public static void log(String TAG, String string) {
        Log.d(TAG, string);
    }

    public static void log(Class clazz, String string) {
        Log.d(clazz.getSimpleName(), string);
    }

    public static void out(String str) {
        System.out.println(str);
    }


    /**
     * 子线程的情况已处理
     */
    public static void toast(final Context context, final String string) {
        if (Thread.currentThread().getName().equals("main")) {
            //如果当前线程是主线程直接显示

            Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (context instanceof Activity) {
            //如果当前线程不是主线程是Activity则用activity的runOnUiThread
            Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }
    public static void toastShort(final Context context, final String string) {
        if (Thread.currentThread().getName().equals("main")) {
            //如果当前线程是主线程直接显示

            Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (context instanceof Activity) {
            //如果当前线程不是主线程是Activity则用activity的runOnUiThread
            Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }
}