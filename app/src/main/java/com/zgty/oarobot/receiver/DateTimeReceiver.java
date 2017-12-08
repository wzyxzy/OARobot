package com.zgty.oarobot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zgty.oarobot.dao.WorkOnOffDaoUtils;
import com.zgty.oarobot.util.DBMakeUtil;
import com.zgty.oarobot.util.WXCPUtils;

import java.io.File;
import java.util.Calendar;

public class DateTimeReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = DateTimeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e(LOG_TAG, "闹钟响了！！！！！！！！！！！！！");
        final WorkOnOffDaoUtils workOnOffDaoUtils = new WorkOnOffDaoUtils(context);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (day == 1) {
            File file = DBMakeUtil.ExportToCSV(workOnOffDaoUtils.queryAll(), month + "月考勤导出表.csv");
            WXCPUtils wxcpUtils = new WXCPUtils(context);
            wxcpUtils.sendText(file, "wuzhiying16", "上个月的考勤导出表发给您，请您用excel导入", "file");
            wxcpUtils.setOnWXCPUtilsListener(new WXCPUtils.OnWXCPUtilsListener() {
                @Override
                public void onSuccess() {
                    workOnOffDaoUtils.clearAll();
                }

                @Override
                public void onError() {

                }
            });

        }


    }
}
