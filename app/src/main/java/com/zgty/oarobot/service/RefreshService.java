package com.zgty.oarobot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zgty.oarobot.R;
import com.zgty.oarobot.bean.GetAccessBack;
import com.zgty.oarobot.common.Constant;

import java.util.Timer;
import java.util.TimerTask;

public class RefreshService extends Service {

    private boolean waitingData;
    private Timer timer;
    private TimerTask timerTask;
    private long timelong;


    public RefreshService() {
        waitingData = true;

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String userid = intent.getStringExtra("userid");
//        timelong = System.currentTimeMillis();
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
//                if (timelong + WAITINGTIME > System.currentTimeMillis()) {
                    if (waitingData) {
                        getDataFromWX(userid);
//                        try {
//                            Thread.sleep(Constant.REFRESHTTIME);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
//                } else {
//                    callbackfinish("timeout");
//                }
            }
        };
        timer.schedule(timerTask, 0, Constant.REFRESHTTIME);
//        while (waitingData) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getDataFromWX(userid);
//
//                }
//            }, Constant.REFRESHTTIME);
//        }
////        getDataFromWX(userid);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                callbackfinish("timeout");
//
//            }
//        }, Constant.WAITINGTIME);
        return super.onStartCommand(intent, flags, startId);
    }

    private void getDataFromWX(final String userid) {
        //?fromUser=wuzhiying88&toUser=ww4c0daf999a9d1a67&createTime=1511921000
        OkGo.<String>post("http://cnbeijing.xyz:8080/wzywx/GetAccess")
                .params("fromUser", userid)
                .params("toUser", getApplicationContext().getString(R.string.weixin_corp_id))
                .params("createTime", System.currentTimeMillis() / 1000 - 5).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                GetAccessBack getAccessBack = new Gson().fromJson(response.body(), GetAccessBack.class);
                if (getAccessBack.getCode() == 0) {
                    callbackfinish(getAccessBack.getResult());
                }
            }


        });
    }

    private void callbackfinish(String result) {
        waitingData = false;
        Intent intent1 = new Intent();
        intent1.putExtra("result", result);
        intent1.setAction(Constant.BROADCASTACTION);
        sendBroadcast(intent1);
        stopTimer();
        onDestroy();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopTimer();
    }
}
