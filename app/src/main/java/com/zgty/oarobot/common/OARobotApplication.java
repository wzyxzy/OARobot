package com.zgty.oarobot.common;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.facebook.stetho.Stetho;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zgty.oarobot.R;

/**
 * Created by zy on 2017/10/31.
 */

public class OARobotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initOARobot();
        initStetho();
    }

    private void initOARobot() {
        StringBuffer param = new StringBuffer();
        param.append(SpeechConstant.APPID + "=" + getString(R.string.app_id));
        SpeechUtility.createUtility(getApplicationContext(), param.toString());
//        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=59f2e233");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

}
